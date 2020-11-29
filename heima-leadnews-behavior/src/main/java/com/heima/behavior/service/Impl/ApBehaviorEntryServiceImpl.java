package com.heima.behavior.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.behavior.mapper.ApBehaviorEntryMapper;
import com.heima.behavior.service.ApBehaviorEntryService;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ApBehaviorEntryServiceImpl extends ServiceImpl<ApBehaviorEntryMapper, ApBehaviorEntry> implements ApBehaviorEntryService {
    /**
     * 根据用户或设备查询行为实体
     *
     * @param userId
     * @param equipmentId
     * @return
     */
    @Override
    public ApBehaviorEntry findByUserIdOrEquipmentId(Integer userId, Integer equipmentId) {
        //根据用户id查询实体
        //type   0终端设备\r\n            1用户',
        if (userId!=null) {
            ApBehaviorEntry apBehaviorEntry = getOne(Wrappers.<ApBehaviorEntry>lambdaQuery().eq(ApBehaviorEntry::getEntryId, userId).eq(ApBehaviorEntry::getType,1));
            return apBehaviorEntry;
        }
        //根据机器id查询实体
        if (userId==null && equipmentId!=0&& equipmentId!=null) {
            ApBehaviorEntry apBehaviorEntry = getOne(Wrappers.<ApBehaviorEntry>lambdaQuery().eq(ApBehaviorEntry::getEntryId, equipmentId).eq(ApBehaviorEntry::getType,0));
            return apBehaviorEntry;
        }

        return null;
    }
}
