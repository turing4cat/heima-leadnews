package com.heima.behavior.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.behavior.mapper.ApFollowBehaviorMapper;
import com.heima.behavior.service.ApBehaviorEntryService;
import com.heima.behavior.service.ApFollowBehaviorService;
import com.heima.model.behavior.dtos.FollowBehaviorDto;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApFollowBehavior;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class ApFollowBehaviorServiceImpl extends ServiceImpl<ApFollowBehaviorMapper, ApFollowBehavior> implements ApFollowBehaviorService {
    @Autowired
    ApBehaviorEntryService apBehaviorEntryService;
    /**
     * 存储关注数据
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveFollowBehavior(FollowBehaviorDto dto) {
        //查询实体  就是当前的登录对象  或者机器
        ApBehaviorEntry behaviorEntry = apBehaviorEntryService.findByUserIdOrEquipmentId(dto.getUserId(), null);
        if (behaviorEntry==null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //如果有  记录用户的行为
        ApFollowBehavior apFollowBehavior = new ApFollowBehavior();
        apFollowBehavior.setArticleId(dto.getArticleId());
        apFollowBehavior.setCreatedTime(new Date());
        apFollowBehavior.setEntryId(behaviorEntry.getEntryId());
        apFollowBehavior.setFollowId(dto.getFollowId());
        save(apFollowBehavior);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
