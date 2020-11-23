package com.heima;

import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.wemedia.WemediaApplication;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class myBatisPlusTest {
    @Autowired
    WmNewsMapper wmNewsMapper;
    @Test
    public void test(){
        WmNews wmNews = new WmNews();
        wmNews.setCreatedTime(new Date());
        wmNews.setEnable((short)1);
        wmNewsMapper.insert(wmNews);
        System.out.println("wmNews = " + wmNews);
    }
}
