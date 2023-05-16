package com.zfl19.quartz.properties;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

/**
 * @author 19zfl
 * @date 2023/5/16
 */
public class QuartzProperties {

    // 使用java程序修改quartz.properties配置信息
    public static void main(String[] args) {

        // 创建工厂实例
        StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
        // 创建配置工厂的属性对象
        Properties props = new Properties();
        props.put(StdSchedulerFactory.PROP_THREAD_POOL_CLASS, "org.quartz.simpl.SimpleThreadPool");
        props.put("org.quartz.threadPool.threadCount", "-1");   // 修改线程数，规则是不少于0，启动报错说明修改成功
        // 使用定义的属性初始化工厂
        try {
            stdSchedulerFactory.initialize(props);
            Scheduler scheduler = stdSchedulerFactory.getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

    }

}
