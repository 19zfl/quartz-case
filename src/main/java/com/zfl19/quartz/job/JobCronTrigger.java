package com.zfl19.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 19zfl
 * @date 2023/5/16
 */
@PersistJobDataAfterExecution
@Slf4j
public class JobCronTrigger implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 输出当前时间
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(date);
        log.info("now time : " + format);
    }

}
