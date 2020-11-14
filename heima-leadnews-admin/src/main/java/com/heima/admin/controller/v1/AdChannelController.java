package com.heima.admin.controller.v1;

import com.alibaba.fastjson.serializer.AdderSerializer;
import com.heima.admin.service.AdChannelService;
import com.heima.apis.admin.AdChannelControllerApi;
import com.heima.model.admin.dtos.AdChannel;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/channel")
public class AdChannelController implements AdChannelControllerApi {
    @Autowired
    private AdChannelService adChannelService;

    @PostMapping("/list")
    @Override
    public ResponseResult findByNameAndPage(@RequestBody ChannelDto dto) {
        return adChannelService.findByNameAndPage(dto);
    }
    @PostMapping("/save")
    @Override
    public ResponseResult insert(@RequestBody AdChannel channel) {
        return adChannelService.insert(channel);
    }
    @PostMapping("/update")
    @Override
    public ResponseResult update(@RequestBody AdChannel channel) {
        return adChannelService.update(channel);
    }
    @GetMapping("/del/{id}")
    @Override
    public ResponseResult deleteById(@PathVariable("id") Integer id) {
        return adChannelService.deleteById(id);
    }
}
