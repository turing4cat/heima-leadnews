package com.heima.apis.article;

import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ApArticleControllerApi {
    /**
     * 保存app文章
     * @param articleDto
     * @return
     */
    ResponseResult saveArticle(ArticleDto articleDto);
}
