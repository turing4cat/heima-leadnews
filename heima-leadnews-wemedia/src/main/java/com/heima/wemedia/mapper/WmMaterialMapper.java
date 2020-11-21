package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import org.apache.ibatis.annotations.Mapper;

//上传的图片管理
@Mapper
public interface WmMaterialMapper extends BaseMapper<WmMaterial> {
}
