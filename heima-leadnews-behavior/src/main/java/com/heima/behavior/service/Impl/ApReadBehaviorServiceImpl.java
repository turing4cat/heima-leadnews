package com.heima.behavior.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.behavior.mapper.ApBehaviorEntryMapper;
import com.heima.behavior.mapper.ApReadBehaviorMapper;
import com.heima.behavior.service.ApBehaviorEntryService;
import com.heima.behavior.service.ApReadBehaviorService;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApReadBehavior;
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
public class ApReadBehaviorServiceImpl extends ServiceImpl<ApReadBehaviorMapper, ApReadBehavior> implements ApReadBehaviorService {
    @Autowired
    ApBehaviorEntryService apBehaviorEntryService;
    /**
     * 保存或更新阅读行为
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult readBehavior(ReadBehaviorDto dto) {
        //判断参数
        if (dto==null || dto.getArticleId()==null ||dto.getEquipmentId()==null) {
            ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //当前的登录对象
        ApUser user = AppThreadLocalUtils.getUser();
        //查询当前的实体
        ApBehaviorEntry behaviorEntry = apBehaviorEntryService.findByUserIdOrEquipmentId(user.getId(),dto.getEquipmentId());
        if (behaviorEntry==null) {
            ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //操作记录阅读行为之前先查询
        ApReadBehavior apReadBehavior = getOne(Wrappers.<ApReadBehavior>lambdaQuery().eq(ApReadBehavior::getEntryId, behaviorEntry.getEntryId()).eq(ApReadBehavior::getArticleId, dto.getArticleId()));
        //判断是否有记录  没有则新增 有则把阅读次数加一
        if (apReadBehavior==null) {
            ApReadBehavior apReadBehavior1 = new ApReadBehavior();
            apReadBehavior1.setArticleId(dto.getArticleId());
            apReadBehavior1.setCount(dto.getCount());
            apReadBehavior1.setCreatedTime(new Date());
            apReadBehavior1.setEntryId(behaviorEntry.getEntryId());
            apReadBehavior1.setLoadDuration(dto.getLoadDuration());
            apReadBehavior1.setPercentage(dto.getPercentage());
            apReadBehavior1.setReadDuration(dto.getReadDuration());
            save(apReadBehavior1);
        }else {
            //修改阅读次数加一
            apReadBehavior.setCount((short)(apReadBehavior.getCount()+1));
            updateById(apReadBehavior);
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
