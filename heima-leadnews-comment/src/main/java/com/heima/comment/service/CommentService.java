package com.heima.comment.service;

import com.heima.model.comment.dtos.CommentDto;
import com.heima.model.comment.dtos.CommentLikeDto;
import com.heima.model.comment.dtos.CommentSaveDto;
import com.heima.model.common.dtos.ResponseResult;

public interface CommentService {
    /**
     * 保存评论
     * @param dto
     * @return
     */
    public ResponseResult saveComment(CommentSaveDto dto);
    /**
     * 点赞某一条评论
     * @param dto
     * @return
     */
    public ResponseResult like(CommentLikeDto dto);
    /**
     * 查询评论
     * @param dto
     * @return
     */
    public ResponseResult findByArticleId(CommentDto dto);
}
