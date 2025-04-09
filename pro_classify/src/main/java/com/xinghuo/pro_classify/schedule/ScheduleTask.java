package com.xinghuo.pro_classify.schedule;

import com.xinghuo.pro_classify.service.LitterImageService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {
    private final LitterImageService litterImageService;

    public ScheduleTask(LitterImageService litterImageService) {
        this.litterImageService = litterImageService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void removeUselessImage() throws Exception {
        litterImageService.removeUselessImage();
    }
}
