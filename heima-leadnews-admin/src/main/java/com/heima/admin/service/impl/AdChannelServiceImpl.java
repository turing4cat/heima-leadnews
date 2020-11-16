package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdChannelMapper;
import com.heima.admin.service.AdChannelService;
import com.heima.model.admin.pojo.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    @Override
    public ResponseResult insert(AdChannel channel) {
        //判断数据
        if (channel==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //封装数据
        channel.setCreatedTime(new Date());
        //执行
        save(channel);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult update(AdChannel channel) {
        //判断参数
        if (null==channel || channel.getId()==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //从数据库查询的数据
        AdChannel byId = getById(channel.getId());
        //判断是否更改名称
        if (byId.getName().equals(channel.getName())){
            updateById(channel);
            return ResponseResult.setAppHttpCodeEnum(AppHttpCodeEnum.SUCCESS);
        }
        //更改的话判断用户名是否存在
        LambdaQueryWrapper<AdChannel> adChannelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        adChannelLambdaQueryWrapper.eq(AdChannel::getName,channel.getName());
        AdChannel channel1 = getOne(adChannelLambdaQueryWrapper);

        if (channel1!=null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH,"用户名已经存在");
        }
        //不存在就修改数据
        updateById(channel);
        return ResponseResult.setAppHttpCodeEnum(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult deleteById(Integer id) {
        //判断参数
        if (id==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //判断是否存在
        AdChannel channelDB = getById(id);
        if (channelDB==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        //判断是否有效
        if (channelDB.getStatus()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH,"频道有效，不能删除");
        }
        removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
