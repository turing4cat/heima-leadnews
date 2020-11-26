package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.model.admin.pojo.AdUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojo.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserLoginService;
import com.heima.utils.common.AppJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;

@Service
@Transactional
public class ApUserLoginServiceImpl implements ApUserLoginService {
    @Autowired
    ApUserMapper apUserMapper;
    /**
     * app端登录
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult login(LoginDto dto) {
        //判断参数
        if (dto.getEquipmentId()==null ||StringUtils.isBlank(dto.getPhone()) ||StringUtils.isBlank(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户名或密码不存在");
        }
        //查询用户
        if (!StringUtils.isBlank(dto.getPhone())&&!StringUtils.isBlank(dto.getPassword())){
            ApUser one = apUserMapper.selectOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, dto.getPhone()));
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
        }else {
            //没有账户
            if (dto.getEquipmentId()==null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("token",AppJwtUtil.getToken(0l));
            return ResponseResult.okResult(map);
        }
    }
}
