package com.heima.behavior.kafka;

import com.alibaba.fastjson.JSON;
import com.heima.behavior.service.ApFollowBehaviorService;
import com.heima.common.message.FollowBehaviorConstants;
import com.heima.model.behavior.dtos.FollowBehaviorDto;
import com.heima.model.behavior.pojos.ApFollowBehavior;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FollowBehaviorListener {
    @Autowired
    ApFollowBehaviorService apFollowBehaviorService;

    @KafkaListener(topics = FollowBehaviorConstants.FOLLOW_BEHAVIOR_TOPIC)
    public void receiverMessage(ConsumerRecord<?, ?> record) {
        Optional<? extends ConsumerRecord<?, ?>> record1 = Optional.ofNullable(record);
        if (record1.isPresent()) {
            Object value = record.value();
            FollowBehaviorDto followBehaviorDto = JSON.parseObject(value.toString(), FollowBehaviorDto.class);
            apFollowBehaviorService.saveFollowBehavior(followBehaviorDto);
        }
    }
}
