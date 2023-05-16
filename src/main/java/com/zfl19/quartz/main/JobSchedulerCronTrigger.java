package com.zfl19.quartz.main;

import com.zfl19.quartz.job.JobCronTrigger;
import com.zfl19.quartz.job.JobSimpleTrigger;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

/**
 * @author 19zfl
 * @date 2023/5/15
 */
@Slf4j
public class JobSchedulerCronTrigger {

    public static void main(String[] args) throws Exception {

        // 调度器（Scheduler），从工厂中获取调度实例
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        Date startDate = new Date(); // 开始执行时间
        Date endDate = new Date(); // 结束执行时间
        endDate.setTime(endDate.getTime() + 10000); // 开始执行时间10秒后结束
        // 任务实例（JobDetail）
        JobDetail jobDetail = JobBuilder.newJob(JobCronTrigger.class) // 加载任务类，与HelloJob完成绑定，要求重写org.quartz.Job类的execute方法
                .withIdentity("job1", "job-group1") // 参数1:任务的名称（唯一标识），参数2：任务组的名称
                .usingJobData("message", "data01")
                .build();

        // 触发器（Trigger）
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "trigger-group1") // 参数1:触发器的名称（唯一标识），参数2：触发器组的名称
                .startNow() // 马上启动
                .withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?"))
                .build();
        // 让调度器关联任务和触发器，保证按照触发器定义的条件执行任务
        scheduler.scheduleJob(jobDetail, trigger);

        scheduler.start();
        // 执行两秒后自动挂起
        Thread.sleep(2000L);
        // 挂起
        scheduler.standby();
        // 执行5秒后自动开启
        Thread.sleep(5000L);
        // 启动
        scheduler.start();
        // 关闭
//        scheduler.shutdown();
    }

}
