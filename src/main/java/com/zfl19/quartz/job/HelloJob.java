package com.zfl19.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 19zfl
 * @date 2023/5/15
 */
@Slf4j
//@PersistJobDataAfterExecution
public class HelloJob implements Job {

    public HelloJob() {
        System.out.println("任务被触发：");
    }

    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    private Integer count;

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 获取JobDataMap信息
        /*String messageByJobDetail = jobExecutionContext.getJobDetail().getJobDataMap().getString("message");
        String messageByTrigger = jobExecutionContext.getTrigger().getJobDataMap().getString("message");
        log.info(messageByJobDetail);
        log.info(messageByTrigger);*/

        // 获取当前Job的执行时间
        /*Date fireTime = jobExecutionContext.getFireTime();
        SimpleDateFormat simpleDateFormatJob = new SimpleDateFormat();
        String formatDate = simpleDateFormatJob.format(fireTime);
        log.info(formatDate);*/

        // 获取jobDetail内容
        /*JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
        String name = jobKey.getName();     // 获取任务实例名称
        String group = jobKey.getGroup();   // 获取任务实例组名称
        log.info(name);
        log.info(group);
        String className = jobExecutionContext.getJobDetail().getJobClass().getName();
        String simpleClassName = jobExecutionContext.getJobDetail().getJobClass().getSimpleName();
        log.info(className);    // 获取任务实例绑定的任务Job类（全限定名形式）
        log.info(simpleClassName);  // 获取任务实例绑定的任务Job类（类名形式）*/

        // 输出当前时间
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(date);
        log.info(format);

        // 通过setter方法获取message值
        /*log.info(message);*/

        ++count;
        log.info(count.toString());

        jobExecutionContext.getJobDetail().getJobDataMap().put("count", count);
    }

}
