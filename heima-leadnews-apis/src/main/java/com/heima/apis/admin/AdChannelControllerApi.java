package com.heima.apis.admin;

import com.heima.model.admin.pojo.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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
    /**
     * 查询所有频道
     * @return
     */
    public ResponseResult findAll();
}