package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdSensitiveMapper;
import com.heima.admin.service.AdSensitiveService;
import com.heima.model.admin.dtos.SensitiveDto;
import com.heima.model.admin.pojo.AdSensitive;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AdSensitiveServiceImpl extends ServiceImpl<AdSensitiveMapper, AdSensitive> implements AdSensitiveService {
    @Override
    public ResponseResult findByNameAndPage(SensitiveDto dto) {
        if (dto==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //查询条件没有的话初始化
        dto.checkParam();
        //条件分页查询
        Page page = new Page(dto.getPage(), dto.getSize());
        //构建查询条件
        LambdaQueryWrapper<AdSensitive> adSensitiveLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNoneBlank(dto.getName())) {
            adSensitiveLambdaQueryWrapper.like(AdSensitive::getSensitives, dto.getName());
        }
        //执行条件分页查询
        IPage page1 = page(page, adSensitiveLambdaQueryWrapper);
        //结果封装
        ResponseResult pageResponseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page1.getTotal());
        pageResponseResult.setData(page1.getRecords());
        return pageResponseResult;
    }

    @Override
    public ResponseResult insert(AdSensitive sensitive) {
        if (sensitive==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        save(sensitive);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult update(AdSensitive sensitive) {
        //判断参数
        if (null==sensitive || sensitive.getId()==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        updateById(sensitive);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult deleteById(Integer id) {
        if (id==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //先判断是否有这条数据
        AdSensitive byId = getById(id);
        if (byId==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
