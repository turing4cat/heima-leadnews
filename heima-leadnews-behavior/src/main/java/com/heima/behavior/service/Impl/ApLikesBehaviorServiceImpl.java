package com.heima.behavior.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.behavior.mapper.ApLikesBehaviorMapper;
import com.heima.behavior.service.ApBehaviorEntryService;
import com.heima.behavior.service.ApLikesBehaviorService;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApLikesBehavior;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class ApLikesBehaviorServiceImpl extends ServiceImpl<ApLikesBehaviorMapper, ApLikesBehavior> implements ApLikesBehaviorService {
    @Autowired
    ApBehaviorEntryService apBehaviorEntryService;
    /**
     * 保存点赞行为
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult like(LikesBehaviorDto dto) {
        //检查参数
        if (dto == null || dto.getArticleId() == null || (dto.getType() < 0 || dto.getType() > 2) || (dto.getOperation() > 1 || dto.getOperation() < 0)) {
            ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        Integer uid = AppThreadLocalUtils.getUser().getId();
        //检查当前登录的实体
        ApBehaviorEntry behaviorEntry = apBehaviorEntryService.findByUserIdOrEquipmentId(uid, dto.getEquipmentId());
        if (behaviorEntry==null) {
            ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //每次构造关系之前先进行查询判断--养成习惯
        ApLikesBehavior apLikesBehavior = getOne(Wrappers.<ApLikesBehavior>lambdaQuery().eq(ApLikesBehavior::getEntryId, behaviorEntry.getEntryId()).eq(ApLikesBehavior::getArticleId, dto.getArticleId()));
        //如果没有  并且还取消点既喜欢  则返回错误
        if (apLikesBehavior==null && dto.getOperation().equals(ApLikesBehavior.Operation.CANCEL.getCode())) {
            ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if (apLikesBehavior==null && dto.getOperation().equals(ApLikesBehavior.Operation.LIKE.getCode())) {
            //判断参数没有 并且为点击喜欢 则构造关系
            ApLikesBehavior apLikesBehavior1 = new ApLikesBehavior();
            apLikesBehavior1.setArticleId(dto.getArticleId());
            apLikesBehavior1.setCreatedTime(new Date());
            apLikesBehavior1.setEntryId(behaviorEntry.getEntryId());
            apLikesBehavior1.setOperation(dto.getOperation());
            apLikesBehavior1.setType(dto.getType());
            save(apLikesBehavior1);
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }else {
            //此处则是取消点赞的操作
            apLikesBehavior.setOperation(dto.getOperation());
//            更新对象信息导数据库
            updateById(apLikesBehavior);
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
    }
}
