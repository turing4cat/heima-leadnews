package com.heima.apis.admin;

import com.heima.model.admin.dtos.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.nio.channels.Channel;

@Api(value = "频道管理",tags = "channel",description = "频道管理的API")
public interface AdChannelControllerApi {

    /**
     * 根据名称分页查询频道列表
     * @param dto
     * @return
     */
    @ApiOperation("频道分页列表查询")
    public ResponseResult findByNameAndPage(ChannelDto dto);

    /**
     * 新增频道
     * @param channel
     * @return
     */
    public ResponseResult insert(AdChannel channel);
    /**
     * 修改频道
     * @param channel
     * @return
     */
    public ResponseResult update(AdChannel channel);
    /**
     * 删除频道
     * @param id
     * @return
     */
    public ResponseResult deleteById(Integer id);
}