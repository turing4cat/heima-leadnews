package com.heima.admin.job;

import com.heima.admin.feign.WemediaFeign;
import com.heima.admin.service.WemediaNewsAutoScanService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class WeMediaNewsAutoScanJob {
    @Autowired
    WemediaNewsAutoScanService wemediaNewsAutoScanService;
    @Autowired
    WemediaFeign wemediaFeign;

    @XxlJob("wemediaAutoScanJob")
    public ReturnT<String> autoScanJob(String param) {
        //查询需要自动发表的id集合
        List<Integer> release = wemediaFeign.findRelease();
        //修改状态   自动任务
        if (release != null && !release.isEmpty()) {
            for (Integer integer : release) {
                wemediaNewsAutoScanService.autoScanByMediaNewsId(integer);
            }
        }
        log.info("自媒体文章审核调度任务执行结束....");
        return ReturnT.SUCCESS;
    }
}
