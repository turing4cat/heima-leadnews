package com.heima.behavior.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.pojos.ApLikesBehavior;
import com.heima.model.common.dtos.ResponseResult;

public interface ApLikesBehaviorService extends IService<ApLikesBehavior> {
    /**
     * 保存点赞行为
     * @param dto
     * @return
     */
    ResponseResult like(LikesBehaviorDto dto);
}
