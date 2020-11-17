package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.AuthDto;
import com.heima.model.user.pojo.ApUserRealname;
import com.heima.user.mapper.ApUserRealnameMapper;
import com.heima.user.service.ApUserRealnameService;
import org.springframework.stereotype.Service;

@Service
public class ApUserRealnameImpl extends ServiceImpl<ApUserRealnameMapper, ApUserRealname> implements ApUserRealnameService {
    @Override
    public ResponseResult loadListByStatus(AuthDto dto) {
        //判断参数
        if (dto==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //分页检查
        dto.checkParam();
        //构造条件
        IPage objectPage = new Page(dto.getPage(),dto.getSize());
        LambdaQueryWrapper<ApUserRealname> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (dto.getStatus()!=null) {
            lambdaQueryWrapper.eq(ApUserRealname::getStatus, dto.getStatus());
        }
        //查询数据
        IPage page = page(objectPage, lambdaQueryWrapper);
        //数据封装
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }
}
