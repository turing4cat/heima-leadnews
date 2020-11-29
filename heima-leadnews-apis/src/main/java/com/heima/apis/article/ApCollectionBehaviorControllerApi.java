package com.heima.apis.article;

import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.behavior.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ApCollectionBehaviorControllerApi {

    /**
     * 加载首页文章
     * @return
     */
    public ResponseResult collectionBehavior(CollectionBehaviorDto dto);
}
