package com.heima.admin.controller.v1;

import com.heima.admin.service.AdSensitiveService;
import com.heima.apis.admin.AdSensitiveControllerApi;
import com.heima.model.admin.dtos.SensitiveDto;
import com.heima.model.admin.pojo.AdSensitive;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sensitive")
public class AdSensitiveController implements AdSensitiveControllerApi {
    @Autowired
    AdSensitiveService adSensitiveService;
    @PostMapping("/list")
    @Override
    public ResponseResult findByNameAndPage(@RequestBody SensitiveDto dto) {
        return adSensitiveService.findByNameAndPage(dto);
    }
    @PostMapping("/save")
    @Override
    public ResponseResult insert(@RequestBody AdSensitive sensitive) {
        return adSensitiveService.insert(sensitive);
    }
    @PostMapping("/update")
    @Override
    public ResponseResult update(@RequestBody AdSensitive sensitive) {
        return adSensitiveService.update(sensitive);
    }
    @DeleteMapping("/del/{id}")
    @Override
    public ResponseResult deleteById(@PathVariable("id") Integer id) {
        return adSensitiveService.deleteById(id);
    }
}
