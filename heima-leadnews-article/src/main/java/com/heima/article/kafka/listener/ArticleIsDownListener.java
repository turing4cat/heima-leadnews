package com.heima.article.kafka.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.service.ApArticleConfigService;
import com.heima.article.service.AuthorService;
import com.heima.common.constants.message.WmNewsMessageConstants;
import com.heima.model.article.pojos.ApArticleConfig;
import org.apache.avro.data.Json;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class ArticleIsDownListener {
    @Autowired
    ApArticleConfigService apArticleConfigService;
    @KafkaListener(topics = WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC)
    public void resMsg(ConsumerRecord<?,?> record){
        Optional<? extends ConsumerRecord<?, ?>> record1 = Optional.ofNullable(record);
        if (record1.isPresent()) {
            String value = (String) record.value();
            Map map = JSON.parseObject(value, Map.class);
            //此处的上下架和自媒体端的相反  所以取反
            boolean isDown=true;
            Object enable = map.get("enable");
            if (enable.equals(1)) {
                isDown=false;
            }
            //根据作者id修改文章的状态
            apArticleConfigService.update(Wrappers.<ApArticleConfig>lambdaUpdate().eq(ApArticleConfig::getArticleId,map.get("articleId")).set(ApArticleConfig::getIsDown,isDown));
        }
    }
}
