package com.heima.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.admin.feign.ArticleFeign;
import com.heima.admin.feign.WemediaFeign;
import com.heima.admin.mapper.AdChannelMapper;
import com.heima.admin.service.AdChannelService;
import com.heima.admin.service.AdSensitiveService;
import com.heima.admin.service.WemediaNewsAutoScanService;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.fastdfs.FastDFSClient;
import com.heima.model.admin.pojo.AdChannel;
import com.heima.model.admin.pojo.AdSensitive;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.SensitiveWordUtil;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
@Service
@Transactional
public class WemediaNewsAutoScanServiceImpl implements WemediaNewsAutoScanService {
    @Autowired
    WemediaFeign wemediaFeign;
    /**
     * 自媒体文章审核
     *
     * @param id
     */
    @GlobalTransactional
    @Override
    public void autoScanByMediaNewsId(Integer id) {
        //新增文章的时候调用该逻辑
        //文章的自动审核逻辑  此处传入的是文章的id
        if (id==null) {
            throw new RuntimeException("文章的id不存在");
        }
        //判断是否存在文章
        WmNews wmNews = wemediaFeign.findById(id);
        if (wmNews==null) {
            throw new RuntimeException("文章不存在");
        }
        //文章的状态判断 是4 代表人工通过 保存到文章 并且为当前的发布时间  小于等于当前时间
        if (wmNews.getStatus()==4 && wmNews.getPublishTime().getTime()<=System.currentTimeMillis()) {
            saveAppArticle(wmNews);
            return;
        }
        if (wmNews.getStatus()==8 && wmNews.getPublishTime().getTime()<=System.currentTimeMillis()) {
            saveAppArticle(wmNews);
            return;
        }
        //如果当前的状态是1 则进行文章的自动审核
        if (wmNews.getStatus()==1) {
            //审核文章的所有图片和内容
            Map<String,Object> contentAndImagesResult=handleTextAndImages(wmNews);
            //交给阿里的接口进行审核 修改状态
           boolean isTextScan= handleTextScan((String) contentAndImagesResult.get("content"),wmNews);
            if (!isTextScan) {
                return;
            }
           boolean isImageScan= handleImageScan((List<String>) contentAndImagesResult.get("image"),wmNews);
            if (!isImageScan) {
                return;
            }
            //本地敏感词管理的审核
            boolean isSensitive= handleSensitive((String) contentAndImagesResult.get("content"),wmNews);
            if (!isSensitive) {
                return;
            }
            //如果发布时间大于本地时间 修改为待发布的状态
            if (wmNews.getPublishTime().getTime()>System.currentTimeMillis()) {
                updateWmNews(wmNews,(short)8,"审核通过，待发bu");
            }
            //走到这里代表审核通过  保存数据到app端
            //修改文章的审核状态的操作   写在了保存app文章中
//            updateWmNews(wmNews,(short)9,"审核通过，待发bu");
            saveAppArticle(wmNews);
        }
    }
    @Autowired
    AdSensitiveService adSensitiveService;
    /**
     * 本地敏感词的审核
     * @param content
     * @param wmNews
     * @return
     */
    private boolean handleSensitive(String content, WmNews wmNews) {
        boolean flag=true;
        List<AdSensitive> adSensitiveList = adSensitiveService.list(Wrappers.<AdSensitive>lambdaQuery().select(AdSensitive::getSensitives));
        //把本地的敏感词存储为集合
        List<String> adSensitives = adSensitiveList.stream().map(x->x.getSensitives()).collect(Collectors.toList());
        //初始化敏感词库
        SensitiveWordUtil.initMap(adSensitives);
        //词库和文章校验
        Map<String, Integer> stringIntegerMap = SensitiveWordUtil.matchWords(content);
        if (stringIntegerMap.size()>0) {
            //修改自媒体库的状态
            updateWmNews(wmNews, (short) 2,"含有敏感词"+stringIntegerMap);
            flag=false;
        }
        return flag;
    }

    @Autowired
    GreenImageScan greenImageScan;
    @Autowired
    FastDFSClient fastDFSClient;
    @Value("${fdfs.url}")
    String fileServerUrl;
    /**
     * 阿里校验图片是否违规
     * @param images
     * @param wmNews
     * @return
     */
    private boolean handleImageScan(List<String> images, WmNews wmNews) {
        boolean flag=true;
        if (images==null ||images.size()==1) {
            return true;
        }
        //获取图片地址  下载图片   校验图片
        ArrayList<byte[]> ImageBytes = new ArrayList<>();
        //需要两个参数  分组 和路径
        try {
            for (String image : images) {
                String imageReplace = image.replace(fileServerUrl, "");
                //获取第一个/ 的下标
                int i = imageReplace.indexOf("/");
                //截取所在的组
                String group = imageReplace.substring(0, i);
                //截取路径
                String path = imageReplace.substring(i + 1);
                byte[] imageBytes = fastDFSClient.download(group, path);
                //校验
                ImageBytes.add(imageBytes);
            }
            //校验图片的合法性
            Map map = greenImageScan.imageScan(ImageBytes);
            //审核不通过
            if (map.get("suggestion").equals("block")) {
                updateWmNews(wmNews, (short) 2, "文章图片有违规");
                flag = false;
            }
            //人工审核
            if (map.get("suggestion").equals("review")) {
                updateWmNews(wmNews, (short) 3, "文章图片有不确定元素");
                flag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag=false;
        }
        return flag;
    }

    @Autowired
    GreenTextScan greenTextScan;
    /**
     * 阿里校验文本的合法性
     * @param content
     * @param wmNews
     * @return
     */
    private boolean handleTextScan(String content, WmNews wmNews) {
        boolean flag=true;
        try {
            Map map = greenTextScan.greeTextScan(content);
            if (map.get("suggestion").equals("block")) {
                //修改自媒体发布的状态和驳回信息
                updateWmNews(wmNews,(short)2,"含有敏感词汇");
                flag=false;
            }
            if (map.get("suggestion").equals("review")) {
                updateWmNews(wmNews,(short)3,"含有不确定词汇,进行人工审核");
                flag=false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag=false;
        }
        return flag;
    }

    /**
     * 审核修改自媒体文章表
     * @param wmNews
     * @param status
     * @param msg
     */
    private void updateWmNews(WmNews wmNews, short status, String msg) {
        wmNews.setStatus(status);
        wmNews.setReason(msg);
        ResponseResult responseResult = wemediaFeign.updateWmNews(wmNews);
        if (!responseResult.getCode().equals(0)) {
            throw new RuntimeException("修改自媒体文章失败");
        }
    }

    /**
     * 抽取文章中的所有内容和图片封装到map中
     * @param wmNews
     * @return
     */
    private Map<String, Object> handleTextAndImages(WmNews wmNews) {
        HashMap<String, Object> contentAndImagesResult = new HashMap<>();
        //文章中的文本信息
        String content = wmNews.getContent();
        //文本字符串
        StringBuilder contentsString = new StringBuilder();
        //图片字符串集合
        ArrayList<String> imagesList = new ArrayList<>();
        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            //获取文本内容
            if (map.get("type").equals("text")) {
                Object value = map.get("value");
                contentsString.append(value);
            }
            //获取图片内容
            if (map.get("type").equals("image")) {
                String value = (String) map.get("value");
                imagesList.add(value);
            }
        }
        //判断封面中是否包含的有图片 有就存储
        //todo 老师写的判断是id
        if (wmNews.getImages()!=null) {
            String[] split = wmNews.getImages().split(",");
            imagesList.addAll(Arrays.asList(split));
        }
        //存储到map中
        contentAndImagesResult.put("content",contentsString.toString());
        contentAndImagesResult.put("image",imagesList);
        return contentAndImagesResult;
    }

    /**
     * 保存app端的文章
     * @param wmNews
     */
    private void saveAppArticle(WmNews wmNews) {
        //保存文章的操作
        ResponseResult responseResult=saveArticle(wmNews);
        if (!responseResult.getCode().equals(0)) {
            throw new RuntimeException("新增文章保存失败");
        }
        //返回文章的id存储到自媒体文章中
        Object data = responseResult.getData();
        wmNews.setArticleId((Long) data);
        //修改文章的状态
        updateWmNews(wmNews,(short)9,"文章审核通过");
    }
    @Autowired
    ArticleFeign articleFeign;
    @Autowired
    AdChannelService adChannelService;
    /**
     * 执行保存文章的调用
     * @param wmNews
     * @return
     */
    private ResponseResult saveArticle(WmNews wmNews) {
        ArticleDto articleDto = new ArticleDto();
        //此处的id作为识别保存或者修改的操作
        if (wmNews.getArticleId()!=null) {
            articleDto.setId(wmNews.getArticleId());
        }
        articleDto.setPublishTime(wmNews.getPublishTime());
        articleDto.setTitle(wmNews.getTitle());
        articleDto.setImages(wmNews.getImages());
        articleDto.setLayout(wmNews.getType());
        articleDto.setContent(wmNews.getContent());
        articleDto.setCreatedTime(new Date());
        //设置频道
        AdChannel channel = adChannelService.getById(wmNews.getChannelId());
        //数据封装
        articleDto.setChannelId(channel.getId());
        articleDto.setChannelName(channel.getName());
        //设置作者信息
        WmUser wmUser = wemediaFeign.findWmUserById(wmNews.getUserId());
        articleDto.setAuthorName(wmUser.getName());
        return  articleFeign.saveArticle(articleDto);
    }
}
