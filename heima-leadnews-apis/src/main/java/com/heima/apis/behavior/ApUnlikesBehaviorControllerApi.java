package com.heima.apis.behavior;

import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.behavior.pojos.ApUnlikesBehavior;
import com.heima.model.common.dtos.ResponseResult;

public interface ApUnlikesBehaviorControllerApi {
    /**
     * 不喜欢行为
     * @return
     */
    public ResponseResult unlikesBehavior(UnLikesBehaviorDto dto);
    /**
     * 根据行为实体id和文章id查询不喜欢行为
     * @param entryId
     * @param articleId
     * @return
     */
    public ApUnlikesBehavior findUnLikeByArticleIdAndEntryId(Integer entryId, Long articleId);
}
