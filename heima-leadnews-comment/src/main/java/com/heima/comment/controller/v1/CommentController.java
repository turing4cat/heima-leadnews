package com.heima.comment.controller.v1;

import com.heima.apis.comment.CommentControllerApi;
import com.heima.comment.service.CommentService;
import com.heima.model.comment.dtos.CommentDto;
import com.heima.model.comment.dtos.CommentLikeDto;
import com.heima.model.comment.dtos.CommentSaveDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comment")
public class CommentController implements CommentControllerApi {
    @Autowired
    CommentService commentService;
    /**
     * 保存评论
     *
     * @param dto
     * @return
     */
    @PostMapping("/save")
    @Override
    public ResponseResult saveComment(@RequestBody CommentSaveDto dto) {
        return commentService.saveComment(dto);
    }

    /**
     * 点赞某一条评论
     *
     * @param dto
     * @return
     */
    @PostMapping("/like")
    @Override
    public ResponseResult like(@RequestBody CommentLikeDto dto) {
        return commentService.like(dto);
    }

    /**
     * 查询评论
     *
     * @param dto
     * @return
     */
    @PostMapping("/load")
    @Override
    public ResponseResult findByArticleId(@RequestBody CommentDto dto) {
        return commentService.findByArticleId(dto);
    }
}
