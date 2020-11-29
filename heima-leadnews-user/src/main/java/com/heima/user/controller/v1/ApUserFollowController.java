package com.heima.user.controller.v1;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.behavior.ApUserFollowControllerApi;
import com.heima.model.user.pojo.ApUserFollow;
import com.heima.user.service.ApUserFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user_follow")
public class ApUserFollowController implements ApUserFollowControllerApi {
    @Autowired
    ApUserFollowService apUserFollowService;
    /**
     * 根据用户id和关注作者的id查询
     *
     * @param userId
     * @param followId
     * @return
     */
    @GetMapping("/one")
    @Override
    public ApUserFollow findByUserIdAndFollowId(@RequestParam("userId") Integer userId,@RequestParam("followId") Integer followId) {
        return apUserFollowService.getOne(Wrappers.<ApUserFollow>lambdaQuery().eq(ApUserFollow::getUserId,userId).eq(ApUserFollow::getFollowId,followId));
    }
}
