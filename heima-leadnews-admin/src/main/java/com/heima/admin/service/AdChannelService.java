package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.common.dtos.ResponseResult;

public interface AdChannelService extends IService<AdChannel> {
    /**
     * 根据名称分页查询频道列表
     * @param dto
     * @return
     */
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
