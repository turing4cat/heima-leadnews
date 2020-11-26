package com.heima.user.controller.v1;

import com.heima.apis.user.UserRelationControllerApi;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.user.service.ApUserRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserRelationController implements UserRelationControllerApi {
    @Autowired
    ApUserRelationService apUserRelationService;
    /**
     * 关注或取消关注
     *
     * @param dto
     * @return
     */
    @PostMapping("/user_follow")
    @Override
    public ResponseResult follow(@RequestBody  UserRelationDto dto) {
        return apUserRelationService.follow(dto);
    }
}
