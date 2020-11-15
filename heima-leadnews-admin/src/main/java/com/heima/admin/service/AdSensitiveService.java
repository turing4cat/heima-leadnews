package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.admin.dtos.SensitiveDto;
import com.heima.model.admin.pojo.AdChannel;
import com.heima.model.admin.pojo.AdSensitive;
import com.heima.model.common.dtos.ResponseResult;

public interface AdSensitiveService extends IService<AdSensitive> {
    /**
     * 根据名称分页查询频道列表
     * @param dto
     * @return
     */
    public ResponseResult findByNameAndPage(SensitiveDto dto);
    /**
     * 新增频道
     * @param sensitive
     * @return
     */
    public ResponseResult insert(AdSensitive sensitive);
    /**
     * 修改频道
     * @param sensitive
     * @return
     */
    public ResponseResult update(AdSensitive sensitive);
    /**
     * 删除频道
     * @param id
     * @return
     */
    public ResponseResult deleteById(Integer id);
}
