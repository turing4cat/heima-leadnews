package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WemediaContants.WemediaContants;
import com.heima.common.exception.CostomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.threadlocal.WmThreadLocalUtils;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {
    //注入前缀  图片的服务器地址
    @Value("${fdfs.url}")
    private String fileServerUrl;

    /**
     * 分页带条件查询自媒体文章列表
     *
     * @param wmNewsPageReqDto
     * @return
     */
    @Override
    public ResponseResult findAll(WmNewsPageReqDto wmNewsPageReqDto) {
        //判断参数
        if (wmNewsPageReqDto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //分页条件的检查
        wmNewsPageReqDto.checkParam();
        //构建条件
        //多条件的查询
        //根据状态   频道  时间范围  关键字模糊查询
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据状态
        if (wmNewsPageReqDto.getStatus() != null) {
            lambdaQueryWrapper.eq(WmNews::getStatus, wmNewsPageReqDto.getStatus());
        }
        //根据频道
        if (wmNewsPageReqDto.getChannelId() != null) {
            lambdaQueryWrapper.eq(WmNews::getChannelId, wmNewsPageReqDto.getChannelId());
        }
        //根据时间范围
        if (wmNewsPageReqDto.getBeginPubDate() != null && wmNewsPageReqDto.getEndPubDate() != null) {
            lambdaQueryWrapper.between(WmNews::getPublishTime, wmNewsPageReqDto.getBeginPubDate(), wmNewsPageReqDto.getEndPubDate());
        }
        //根据关键字模糊查询
        if (wmNewsPageReqDto.getKeyword() != null) {
            lambdaQueryWrapper.like(WmNews::getTitle, wmNewsPageReqDto.getKeyword());
        }
        //保证登录的状态
        WmUser user = WmThreadLocalUtils.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        lambdaQueryWrapper.eq(WmNews::getUserId, user.getId());
        //结果倒序排列
        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);
        //查询数据
        IPage pageParam = new Page(wmNewsPageReqDto.getPage(), wmNewsPageReqDto.getSize());
        //查询
        IPage page = page(pageParam, lambdaQueryWrapper);
        ResponseResult responseResult = new PageResponseResult(wmNewsPageReqDto.getPage(), wmNewsPageReqDto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        responseResult.setHost(fileServerUrl);
        //封装返回
        return responseResult;
    }

    /**
     * 自媒体文章发布
     *
     * @param dto
     * @param isSubmit 是否为提交 1 为提交 0为草稿
     * @return
     */
    @Override
    public ResponseResult saveNews(WmNewsDto dto, Short isSubmit) {
        //检查参数
        if (dto == null || dto.getContent() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //保存或修改文章 前端的类型有三种
        // type 文章布局  0 无图文章    1 单图文章   3 多图文章
        //封装对像
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(dto, wmNews);
        //前端如果是自动的布局 传入的类型是 -1 无法封装 此处把参数置为null
        if (dto.getType().equals(WemediaContants.WM_NEWS_TYPE_AUTO)) {
            wmNews.setType(null);
        }
        //前端传入的图片类型是一个数组  数据库中存储的数据类型格式 使用 ， 号分割
        //group1/M00/00/00/wKjIgl892xmAG_yjAAB6OkkuJd4819.jpg,group1/M00/00/00/wKjIgl892xGANV6qAABzWOH8KDY775.jpg,group1/M00/00/00/wKjIgl892wKAZLhtAASZUi49De0836.jpg
//        修改数据类型
        if (dto.getImages() != null && dto.getImages().size() > 0) {
            List<String> images = dto.getImages();
            String imagesStr = StringUtils.join(dto.getImages().stream()
                    .map(img -> img
                            .replace(fileServerUrl, "")
                            .replace(" ", "")).collect(Collectors.toList()), ",");
            wmNews.setImages(imagesStr);
        }
        //保存或修改文章
        saveWmNews(wmNews, isSubmit);
        //关联关系   此处有两种  文章内容图片和素材    文章封面和素材
        //文章内容图片和素材
//        从文章中找出图片  文章中的图片不是单独存储的
        //此处的存储的是文章中的图片
        List<String> materials = ectractUrlInfo(dto.getContent());
        //当前为提交  图片有内容  就构建关系
        if (isSubmit.equals(WmNews.Status.SUBMIT) && materials.size() > 0) {
            ResponseResult responseResult = saveRelativeInfoForContent(materials, wmNews.getId());
            //如果没有问题相应给用户信息
            if (responseResult != null) {
                return responseResult;
            }
        }
        // 文章封面和素材
        if (isSubmit.equals(WmNews.Status.SUBMIT.getCode())) {
            ResponseResult responseResult = saveRelativeInfoForCover(dto, materials,wmNews);
            //如果没有问题相应给用户信息
            if (responseResult != null) {
                return responseResult;
            }
        }
//        获得到图片的路径集合
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 保存封面和素材的关系
     * @param dto
     * @param materials
     * @param wmNews
     * @return
     */
    private ResponseResult saveRelativeInfoForCover(WmNewsDto dto, List<String> materials, WmNews wmNews) {
        //前端传入的封面素材
        List<String> images = dto.getImages();
        //设置自动匹配封面
        if (dto.getType().equals(WemediaContants.WM_NEWS_TYPE_AUTO)) {
            //当前内容中的图片是大于2 则显示三张封面
            if (materials.size()>2) {
                wmNews.setType(WemediaContants.WM_NEWS_MANY_IMAGE);
                images=materials.stream().limit(3).collect(Collectors.toList());
            }else if (materials.size()>0 && materials.size()<=2){
                //当前内容中的图片是小于等于2 则显示一张
                wmNews.setType(WemediaContants.WM_NEWS_SINGLE_IMAGE);
                images=materials.stream().limit(1).collect(Collectors.toList());
            }else {
                //没有则是无图模式
                wmNews.setType(WemediaContants.WM_NEWS_NONE_IMAGE);
            }
            //修改图片的信息
            //修改文章信息
            if (images!=null && images.size()>0) {
                //去除前缀 使用 ，号分割
                wmNews.setImages(StringUtils.join(images.stream().map(img->img.replace(fileServerUrl,"")).collect(Collectors.toList()), ","));
            }
            updateById(wmNews);
        }
        //构建封面图片和文章的关系
        if (images!=null && images.size()>0) {
            ResponseResult responseResult = saveRelativeInfoForImage(images, wmNews.getId());
            if (responseResult != null) {
                return responseResult;
            }
        }
        return null;
    }

    /**
     * 构建封面图片和素材的关系
     * @param images
     * @param id
     * @return
     */
    private ResponseResult saveRelativeInfoForImage(List<String> images, Integer id) {
        images=images.stream().map(x->x.replace(fileServerUrl,"")).collect(Collectors.toList());
        return saveRelativeInfo(images,id,WemediaContants.WM_COVER_REFERENCE);
    }

    /**
     * 构建关系  文章内容中的图片和素材的关系
     *
     * @param materials
     * @param newsId
     * @return
     */
    private ResponseResult saveRelativeInfoForContent(List<String> materials, Integer newsId) {
        //WemediaContants.WM_CONTENT_REFERENCE内容中的图片
        return saveRelativeInfo(materials, newsId, WemediaContants.WM_CONTENT_REFERENCE);
    }

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    /**
     * 保存中间表  素材id和新闻id
     *
     * @param materials
     * @param newsId
     * @param type
     * @return
     */
    private ResponseResult saveRelativeInfo(List<String> materials, Integer newsId, Short type) {
//       执行保存操作 此处需要有素材id和新闻id
        //根据素材的url获取素材的id
        List<WmMaterial> wmMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, materials).eq(WmMaterial::getUserId, WmThreadLocalUtils.getUser().getId()));
        //判断有没有素材
        if (wmMaterials == null) {
            throw new CostomException(ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "素材没有找到"));
        }
        //根据当前素材的url获取id
        ArrayList<Integer> materialIds = new ArrayList<>();
        Map<String, Integer> materalImgUrlAndId = wmMaterials.stream().collect(Collectors.toMap(WmMaterial::getUrl, WmMaterial::getId));
        for (String material : materials) {
            Integer materialId = materalImgUrlAndId.get(material);
            //如果没有
            if ("null".equals(materialId)) {
                throw new CostomException(ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "素材没有找到"));
            }
            materialIds.add(materialId);
        }
        //执行批量保存的操作
        wmNewsMaterialMapper.saveRelationsByContent(materialIds,newsId,type);
        return null;
    }

    private List<String> ectractUrlInfo(String content) {
        //格式[{"type","image"},{"value","图片路径"}]
        //创建集合存储路径
        ArrayList<String> imgUrl = new ArrayList<>();
        //把数据转成map  通过key获取
        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            //获取image对应的图片信息
            if (map.get("type").equals("image")) {
                String ImgUrl = (String) map.get("value");
                //如果有文件服务器的ip去掉
                ImgUrl.replace(fileServerUrl, "");
                imgUrl.add(ImgUrl);
            }
        }
        return imgUrl;
    }

    @Autowired
    WmNewsMaterialMapper wmNewsMaterialMapper;

    /**
     * 保存或修改文章
     *
     * @param wmNews
     * @param isSubmit
     */
    private void saveWmNews(WmNews wmNews, Short isSubmit) {
        //设置状态草稿或者待审核
        wmNews.setStatus(isSubmit);
        wmNews.setUserId(WmThreadLocalUtils.getUser().getId());
        wmNews.setCreatedTime(new Date());
        //该字段表示上架下架
        wmNews.setEnable((short) 1);
        //在此处判断是修改还是新发表的数据
        if (wmNews.getId() == null) {
            save(wmNews);
        } else {
            //修改  修改的时候先删除关系
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId, wmNews.getId()));
            //修改文章内容
            updateById(wmNews);
        }
    }

    /**
     * 根据文章id查询文章
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult findWmNewsById(Integer id) {
        //检查参数
        if (id==null) {
            ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmNews wmNews = getById(id);
        if (wmNews==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"文章不存在");
        }
        ResponseResult responseResult = ResponseResult.okResult(wmNews);
        responseResult.setHost(fileServerUrl);
        return responseResult;
    }

    /**
     * 删除文章
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult delNews(Integer id) {
        //检查参数
        if (id==null) {
            ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //获取数据
        WmNews wmNews = getById(id);
        if (wmNews==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"文章不存在");
        }
        //判断是否已经上架 已经发布不能删除
//        status==9表示发布  type==1表示上架
        if (wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode()) && wmNews.getEnable().equals(WemediaContants.WM_NEWS_ENABLE_UP)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"文章已发布，不能删除");
        }
        //删除关系
        wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId,wmNews.getId()));
        removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 上下架
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult downOrUp(WmNewsDto dto) {
        //检查参数
        if (dto==null || dto.getId()==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //判断文章是否存在
        WmNews wmNews = getById(dto.getId());

        if (wmNews==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"文章不存在");
        }
        //判断是否已经发布
        if (wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())) {
            //修改文章的状态  上架下架
            wmNews.setEnable(dto.getEnable());
            updateById(wmNews);
        }

        //结果返回
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
