package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.mapper.AuthorMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.article.ArticleConstants;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Service
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {
    @Autowired
    AuthorMapper authorMapper;
    @Autowired
    ApArticleConfigMapper apArticleConfigMapper;
    @Autowired
    ApArticleContentMapper apArticleContentMapper;
    @Value("${fdfs.url}")
    private String fileServerUrl;
    /**
     * 保存或修改文章
     *
     * @param articleDto
     * @return
     */
    @Override
    public ResponseResult saveArtilce(ArticleDto articleDto) {
        //检查参数
        if (articleDto==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //操作三张表  文章 文章配置 文章内容
        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(articleDto,apArticle);
        //保存或修改文章 判断是否存在id 如果是就修改  不是就新增
        if (articleDto.getId()==null) {
            //保存
            //补全作者id
            ApAuthor apAuthor = authorMapper.selectOne(Wrappers.<ApAuthor>lambdaQuery().eq(ApAuthor::getName, articleDto.getAuthorName()));
            if (apArticle!=null) {
                apArticle.setAuthorId(apAuthor.getId().longValue());
            }
            //保存文章
            save(apArticle);
            //保存文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(articleDto.getContent());
            apArticleContentMapper.insert(apArticleContent);
            //保存文章配置
            ApArticleConfig apArticleConfig = new ApArticleConfig();
            apArticleConfig.setArticleId(apArticle.getId());
            apArticleConfig.setIsComment(true);
            apArticleConfig.setIsDelete(true);
            apArticleConfig.setIsDown(false);
            apArticleConfig.setIsForward(false);
            apArticleConfigMapper.insert(apArticleConfig);
        }else {
            //修改文章
            ApArticle apArticleDB = getById(apArticle.getId());
            if (apArticleDB==null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"文章没有找到");
            }
            updateById(apArticle);
            //修改文章的内容
            //先根据作者id 查询到文章内容信息  在修改文章内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, apArticle.getId()));
            apArticleContent.setContent(articleDto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }
        return ResponseResult.okResult(apArticle.getId());
    }
    @Autowired
    ApArticleMapper apArticleMapper;
    // 单页最大加载的数字
    private final static short MAX_PAGE_SIZE = 50;
    /**
     * 根据参数加载文章列表
     *
     * @param loadtype 1为加载更多  2为加载最新
     * @param dto
     * @return
     */
    @Override
    public ResponseResult load(Short loadtype, ArticleHomeDto dto) {
        //参数检验
        Integer size = dto.getSize();
        if (size==0||size==null) {
            size=10;
        }
        size = Math.min(size, MAX_PAGE_SIZE);
        dto.setSize(size);
        //判断参数  如果没有指定加载更多和最新 默认指定加载更多
        if (!loadtype.equals(ArticleConstants.LOADTYPE_LOAD_MORE)&&!loadtype.equals(ArticleConstants.LOADTYPE_LOAD_NEW)) {
            loadtype=ArticleConstants.LOADTYPE_LOAD_MORE;
        }
        //频道没有就给默认值
        if (dto.getTag()==null) {
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }
        //时间校验没有就给当前时间
        if (dto.getMaxBehotTime()==null) dto.setMaxBehotTime(new Date());
        if (dto.getMinBehotTime()==null) dto.setMinBehotTime(new Date());
        //执行sql
        List<ApArticle> apArticles = apArticleMapper.loadArticleList(dto, loadtype);
        ResponseResult responseResult = ResponseResult.okResult(apArticles);
        responseResult.setHost(fileServerUrl);
        return responseResult;
    }

    /**
     * 加载文章详情
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult loadArticleInfo(ArticleInfoDto dto) {
        //检查参数
        if (dto.getArticleId()==null) {
            ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //前端需要的参数有两个config和content  结果前端处理
        // 查询数据
        //文本内容构造查询
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getArticleId()));
        //文本配置构造查询
        ApArticleConfig apArticleConfig = apArticleConfigMapper.selectOne(Wrappers.<ApArticleConfig>lambdaQuery().eq(ApArticleConfig::getArticleId, dto.getArticleId()));
        //结果封装
        HashMap<String, Object> map = new HashMap<>();
        //数据返回封装
        map.put("content",apArticleContent);
        map.put("config",apArticleConfig);
        return ResponseResult.okResult(map);
    }
}