package com.heima.apis.behavior;

import com.heima.model.user.pojo.ApUserFollow;

public interface ApUserFollowControllerApi {

    /**
     * 根据用户id和关注作者的id查询
     * @param userId
     * @param followId
     * @return
     */
    public ApUserFollow findByUserIdAndFollowId(Integer userId, Integer followId);
}
