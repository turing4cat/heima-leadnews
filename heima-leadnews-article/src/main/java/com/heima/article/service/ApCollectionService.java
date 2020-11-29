package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.behavior.dtos.CollectionBehaviorDto;
import com.heima.model.behavior.pojos.ApCollection;
import com.heima.model.common.dtos.ResponseResult;

public interface ApCollectionService extends IService<ApCollection> {
    /**
     * 加载首页文章
     * @return
     */
    public ResponseResult collectionBehavior(CollectionBehaviorDto dto);
}
