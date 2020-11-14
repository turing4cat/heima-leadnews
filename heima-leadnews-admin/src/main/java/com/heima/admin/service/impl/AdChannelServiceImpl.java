package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdChannelMapper;
import com.heima.admin.service.AdChannelService;
import com.heima.model.admin.dtos.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AdChannelServiceImpl extends ServiceImpl<AdChannelMapper, AdChannel> implements AdChannelService {
    /**
     * 分页条件查询
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findByNameAndPage(ChannelDto dto) {
        //判断参数
        if (dto==null) {
            return  ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //分页参数检查  如果没有赋予默认值
        dto.checkParam();
        //根据名称模糊分页查询
        Page page = new Page(dto.getPage(), dto.getSize());
        //构建查询的条件
        LambdaQueryWrapper<AdChannel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //判断查询条件是否为空
        if (StringUtils.isNoneBlank(dto.getName())) {
            //构建查询条件
            lambdaQueryWrapper.like(AdChannel::getName,dto.getName());
        }
        //查询
        IPage result = page(page, lambdaQueryWrapper);

        //封装分页返回的结果
        ResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(), (int) page.getTotal());
        //封装数据
        responseResult.setData(result.getRecords());
        return responseResult;
    }
}
