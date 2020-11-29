package com.heima.behavior.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.behavior.mapper.ApUnlikesBehaviorMapper;
import com.heima.behavior.service.ApBehaviorEntryService;
import com.heima.behavior.service.ApUnlikesBehaviorService;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApUnlikesBehavior;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojo.ApUser;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class ApUnlikesBehaviorServiceImpl extends ServiceImpl<ApUnlikesBehaviorMapper, ApUnlikesBehavior> implements ApUnlikesBehaviorService {
    @Autowired
    ApBehaviorEntryService apBehaviorEntryService;
    /**
     * 不喜欢行为
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult unlikesBehaviorBehavior(UnLikesBehaviorDto dto) {
        //检查参数
        if (dto == null || dto.getArticleId() == null || (dto.getType() < 0 || dto.getType() > 2)) {
            ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //当前登录的用户
        ApUser user = AppThreadLocalUtils.getUser();
        //查询当前登录的实体
        ApBehaviorEntry behaviorEntry= apBehaviorEntryService.findByUserIdOrEquipmentId(user.getId(), dto.getEquipmentId());
        if (behaviorEntry==null) {
            ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //查询是否有不喜欢
        ApUnlikesBehavior apUnlikesBehavior = getOne(Wrappers.<ApUnlikesBehavior>lambdaQuery().eq(ApUnlikesBehavior::getArticleId, dto.getArticleId()).eq(ApUnlikesBehavior::getEntryId, behaviorEntry.getId()));
        if (apUnlikesBehavior==null && dto.getType().equals(ApUnlikesBehavior.Type.UNLIKE.getCode())) {
            //为空则添加数据
            ApUnlikesBehavior apUnlikesBehavior1 = new ApUnlikesBehavior();
            apUnlikesBehavior1.setArticleId(dto.getArticleId());
            apUnlikesBehavior1.setCreatedTime(new Date());
            apUnlikesBehavior1.setEntryId(behaviorEntry.getId());
            apUnlikesBehavior1.setType(dto.getType());
            save(apUnlikesBehavior1);
        }else {
            //则是存在已经点过 取消不喜欢的操作
            apUnlikesBehavior.setType(dto.getType());
            updateById(apUnlikesBehavior);
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
