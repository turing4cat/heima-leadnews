package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dtos.NewsAuthDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.vos.WmNewsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WmNewsMapper extends BaseMapper<WmNews> {
    List<WmNewsVo> findListAndPage(@Param("dto") NewsAuthDto dto);
    int findListCount(@Param("dto") NewsAuthDto dto);
}
