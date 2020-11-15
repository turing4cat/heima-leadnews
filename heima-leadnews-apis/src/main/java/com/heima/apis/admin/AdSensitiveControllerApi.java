package com.heima.apis.admin;

import com.heima.model.admin.dtos.SensitiveDto;
import com.heima.model.admin.pojo.AdSensitive;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "敏感词管理",tags = "AdSensitive",description = "敏感词管理的api")
public interface AdSensitiveControllerApi {
    /**
     * 根据名称分页查询敏感词列表
     * @param dto
     * @return
     */
    @ApiOperation("敏感词分页列表查询")
    public ResponseResult findByNameAndPage(SensitiveDto dto);

    /**
     * 新增敏感词
     * @param sensitive
     * @return
     */
    public ResponseResult insert(AdSensitive sensitive);
    /**
     * 修改敏感词
     * @param sensitive
     * @return
     */
    public ResponseResult update(AdSensitive sensitive);
    /**
     * 删除敏感词
     * @param id
     * @return
     */
    public ResponseResult deleteById(Integer id);
}
