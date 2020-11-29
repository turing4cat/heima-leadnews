package com.heima.model.comment.dtos;

import com.heima.model.common.annotation.IdEncrypt;
import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

import java.util.Date;

@Data
public class CommentDto extends PageRequestDto {

    /**
     * 文章id
     */
    @IdEncrypt
    private Long articleId;

    // 最小时间
    private Date minDate;

    //是否是首页
    private Short index;

}