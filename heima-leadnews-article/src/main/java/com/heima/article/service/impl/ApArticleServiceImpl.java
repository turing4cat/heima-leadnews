package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.mapper.AuthorMapper;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {
    @Autowired
    AuthorMapper authorMapper;
    @Autowired
    ApArticleConfigMapper apArticleConfigMapper;
    @Autowired
    ApArticleContentMapper apArticleContentMapper;
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
}