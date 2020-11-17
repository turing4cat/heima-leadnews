package com.heima.user.controller.v1;

import com.heima.apis.user.ApUserRealnameControllerApi;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.AuthDto;
import com.heima.user.service.ApUserRealnameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class ApUserRealnameController implements ApUserRealnameControllerApi {
    @Autowired
    ApUserRealnameService userRealnameService;
    @PostMapping("/list")
    @Override
    public ResponseResult loadListByStatus(@RequestBody AuthDto dto) {
        return userRealnameService.loadListByStatus(dto);
    }
}
