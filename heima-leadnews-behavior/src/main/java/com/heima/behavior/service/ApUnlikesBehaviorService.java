package com.heima.behavior.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.behavior.pojos.ApUnlikesBehavior;
import com.heima.model.common.dtos.ResponseResult;

public interface ApUnlikesBehaviorService extends IService<ApUnlikesBehavior> {
    /**
     * 不喜欢行为
     * @return
     */
    public ResponseResult unlikesBehaviorBehavior(UnLikesBehaviorDto dto);
}
