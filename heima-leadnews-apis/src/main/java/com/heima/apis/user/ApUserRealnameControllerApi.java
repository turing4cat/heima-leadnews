package com.heima.apis.user;

import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.AuthDto;

public interface ApUserRealnameControllerApi {
    public ResponseResult loadListByStatus(AuthDto dto);
}
