package com.heima.wemedia.controller.v1;

import com.heima.apis.wemedia.WmMaterialControllerApi;
import com.heima.common.constants.WemediaContants.WemediaContants;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.wemedia.service.WmMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/material")
public class WmMaterialController implements WmMaterialControllerApi {
    @Autowired
    WmMaterialService wmMaterialService;
    @PostMapping("/upload_picture")
    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        return wmMaterialService.uploadPicture(multipartFile);
    }
    @PostMapping("/list")
    @Override
    public ResponseResult findList(@RequestBody WmMaterialDto dto) {
        return wmMaterialService.findList(dto);
    }
    @GetMapping("/del_picture/{id}")
    @Override
    public ResponseResult delPicture(@PathVariable("id") Integer id) {
        return wmMaterialService.delPicture(id);
    }
    @GetMapping("/cancel_collect/{id}")
    @Override
    public ResponseResult cancelCollectionMaterial(@PathVariable("id") Integer id) {
        return wmMaterialService.updateCollection(id, WemediaContants.CANCEL_COLLECT_MATERIAL);
    }
    @GetMapping("/collect/{id}")
    @Override
    public ResponseResult collectionMaterial(@PathVariable("id") Integer id) {
        return wmMaterialService.updateCollection(id,WemediaContants.COLLECT_MATERIAL);
    }
}
