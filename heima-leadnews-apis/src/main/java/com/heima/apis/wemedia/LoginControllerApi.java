package com.heima.apis.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmUserDto;
import org.springframework.web.bind.annotation.RequestBody;

public interface LoginControllerApi {
    public ResponseResult login(@RequestBody WmUserDto dto);
}
