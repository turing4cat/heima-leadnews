package com.heima.article.feign;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.model.user.pojo.ApUserFollow;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("leadnews-user")
public interface UserFeign {
    @GetMapping("/api/v1/user_follow/one")
    public ApUserFollow findByUserIdAndFollowId(@RequestParam("userId") Integer userId, @RequestParam("followId") Integer followId);
}
