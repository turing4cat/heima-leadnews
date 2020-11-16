package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.admin.service.AdUserService;
import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.admin.pojo.AdUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AppJwtUtil;
import com.heima.utils.common.BCrypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.sql.Wrapper;
import java.util.HashMap;

@Service
public class AdUserServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements AdUserService {
    @Override
    public ResponseResult login(AdUserDto dto) {
        //判断参数
        if (StringUtils.isBlank(dto.getName()) ||StringUtils.isBlank(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户名或密码不存在");
        }
        //查询用户
        AdUser one = getOne(Wrappers.<AdUser>lambdaQuery().eq(AdUser::getName, dto.getName()));
        if (one==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户名不存在");
        }
        //比对密码
        //前端密码加盐加密和后端密码比较
        //密码不正确
        if (!one.getPassword().equals( DigestUtils.md5DigestAsHex((dto.getPassword() + one.getSalt()).getBytes()))) {
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        //密码正确
        //数据处理
        HashMap<Object, Object> map = new HashMap<>();
        map.put("token", AppJwtUtil.getToken(one.getId().longValue()));
        one.setPassword("");
        one.setSalt("");
        map.put("user",one);
        return ResponseResult.okResult(map);
    }
}
