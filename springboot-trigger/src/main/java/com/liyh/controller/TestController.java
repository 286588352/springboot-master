package com.liyh.controller;

import com.liyh.task.ScheduleTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试接口
 *
 * @author liyh
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ScheduleTask scheduleTask;

    @GetMapping("/updateCron")
    public String updateCron(String cron) {
        System.out.println("修改新的cron表达式：" + cron);
        scheduleTask.setCron(cron);
        return "修改成功";
    }

    @GetMapping("/updateTimer")
    public String updateTimer(Long timer) {
        System.out.println("修改新的timer执行时间：" + timer);
        scheduleTask.setTimer(timer);
        return "修改成功";
    }
}
