package com.heima.behavior.controller.v1;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.behavior.ApUnlikesBehaviorControllerApi;
import com.heima.behavior.service.ApUnlikesBehaviorService;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.behavior.pojos.ApUnlikesBehavior;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/unlike_behavior")
public class ApUnlikesBehaviorController implements ApUnlikesBehaviorControllerApi {
    @Autowired
    ApUnlikesBehaviorService apUnlikesBehaviorService;
    /**
     * 不喜欢行为
     *
     * @param dto
     * @return
     */
    @PostMapping
    @Override
    public ResponseResult unlikesBehavior(@RequestBody UnLikesBehaviorDto dto) {
        return apUnlikesBehaviorService.unlikesBehaviorBehavior(dto);
    }

    /**
     * 根据行为实体id和文章id查询不喜欢行为
     *
     * @param entryId
     * @param articleId
     * @return
     */
    @GetMapping("/one")
    @Override
    public ApUnlikesBehavior findUnLikeByArticleIdAndEntryId(@RequestParam("entryId") Integer entryId,@RequestParam("articleId") Long articleId) {
        return apUnlikesBehaviorService.getOne(Wrappers.<ApUnlikesBehavior>lambdaQuery().eq(ApUnlikesBehavior::getArticleId,articleId).eq(ApUnlikesBehavior::getEntryId,entryId));
    }
}
