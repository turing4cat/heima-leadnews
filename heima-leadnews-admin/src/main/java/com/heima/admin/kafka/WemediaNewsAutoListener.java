package com.heima.admin.kafka;

import com.alibaba.fastjson.JSON;
import com.heima.admin.service.WemediaNewsAutoScanService;
import com.heima.common.constants.NewsAutoScanConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WemediaNewsAutoListener {
    @Autowired
    WemediaNewsAutoScanService wemediaNewsAutoScanService;
    @KafkaListener(topics = NewsAutoScanConstants.WM_NEWS_AUTO_SCAN_TOPIC)
    public void recevieMessage(ConsumerRecord<?,?> record){
        Optional<? extends ConsumerRecord<?, ?>> record1 = Optional.ofNullable(record);
        if (record1.isPresent()){
            Object value = record.value();
            System.out.println("接受参数 ===========> " + Integer.valueOf((String) value));
            wemediaNewsAutoScanService.autoScanByMediaNewsId(Integer.valueOf((String) value));
        }
    }
}
