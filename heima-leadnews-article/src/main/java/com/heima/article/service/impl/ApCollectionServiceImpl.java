package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.feign.ApBehaviorClient;
import com.heima.article.mapper.ApCollectionBehaviorMapper;
import com.heima.article.service.ApCollectionService;
import com.heima.model.behavior.dtos.CollectionBehaviorDto;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApCollection;
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
public class ApCollectionServiceImpl extends ServiceImpl<ApCollectionBehaviorMapper, ApCollection> implements ApCollectionService {
    @Autowired
    ApBehaviorClient apBehaviorClient;
    /**
     * 加载首页文章
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult collectionBehavior(CollectionBehaviorDto dto) {
        //检查参数
        if (dto==null || dto.getEquipmentId()==null) {
            ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApUser user = AppThreadLocalUtils.getUser();
        ApBehaviorEntry behaviorEntry = apBehaviorClient.findByUserIdOrEquipmentId(user.getId(), dto.getEquipmentId());
        //查询是否收藏
        ApCollection apCollection = getOne(Wrappers.<ApCollection>lambdaQuery().eq(ApCollection::getArticleId, dto.getEntryId()).eq(ApCollection::getEntryId, behaviorEntry.getId()));
        if (apCollection==null && dto.getOperation().equals(ApCollection.Type.ARTICLE.getCode())) {
            //收藏操作
            ApCollection apCollection1 = new ApCollection();
            apCollection1.setArticleId(dto.getEntryId());
            apCollection1.setCollectionTime(new Date());
            apCollection1.setEntryId(behaviorEntry.getId());
            apCollection1.setPublishedTime(dto.getPublishedTime());
            apCollection1.setType(dto.getType());
            save(apCollection1);
        }else if(apCollection!=null){
            //修改收藏的状态
            removeById(apCollection.getId());
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
