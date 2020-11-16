package com.heima.apis.admin;

import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.RequestBody;

public interface LoginControllerApi {
    public ResponseResult login(@RequestBody AdUserDto dto);
}
