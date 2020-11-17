package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.AuthDto;
import com.heima.model.user.pojo.ApUserRealname;

public interface ApUserRealnameService extends IService<ApUserRealname> {
    /**
     * 按照状态分页查询用户列表
     * @param dto
     * @return
     */
    public ResponseResult loadListByStatus(AuthDto dto);
    /**
     * 根据状态进行审核
     * @param dto
     * @param status  2 审核失败   9 审核成功
     * @return
     */
    ResponseResult updateStatusById(AuthDto dto, Short status);
}
