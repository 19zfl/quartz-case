package com.zfl19.quartz.main;

import com.zfl19.quartz.job.HelloJob;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author 19zfl
 * @date 2023/5/15
 */
@Slf4j
public class HelloScheduler {

    public static void main(String[] args) throws Exception {

        // 调度器（Scheduler），从工厂中获取调度实例
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        // 任务实例（JobDetail）
        JobDetail jobDetail = JobBuilder.newJob(HelloJob.class) // 加载任务类，与HelloJob完成绑定，要求重写org.quartz.Job类的execute方法
                .withIdentity("job1", "job-group1") // 参数1:任务的名称（唯一标识），参数2：任务组的名称
                .usingJobData("message", "data01")
                .usingJobData("count", 0)
                .build();

//        String group = jobDetail.getKey().getGroup();
//        String name = jobDetail.getKey().getName();
//        String name1 = jobDetail.getJobClass().getName();
//        log.info(group);
//        log.info(name);
//        log.info(name1);

        // 触发器（Trigger）
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "trigger-group1") // 参数1:触发器的名称（唯一标识），参数2：触发器组的名称
                .usingJobData("message", "data02")
                .startNow() // 马上启动
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatSecondlyForever(5))  // 每5秒重复执行一次
                .build();
        // 让调度器关联任务和触发器，保证按照触发器定义的条件执行任务
        scheduler.scheduleJob(jobDetail, trigger);
        // 启动
        scheduler.start();
    }

}
