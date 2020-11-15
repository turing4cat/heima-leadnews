package com.heima.common.exception;

import com.heima.model.common.dtos.ResponseResult;

public class CostomException extends RuntimeException {
    private ResponseResult responseResult;

    public CostomException(ResponseResult responseResult){
        this.responseResult = responseResult;
    }

    public ResponseResult getResponseResult(){
        return responseResult;
    }
}
