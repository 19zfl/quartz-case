# quartz

## 1. quartz模式

- Builder模式
- Factory模式
- 组件模式
- 链式编程

## 2. quartz核心

- 任务Job

  > job就是你想要实现的任务类，每一个job必须实现org.quartz.job接口，且只需要实现接口定义的execute()方法。

- 触发器Trigger

  > Trigger为你执行任务的触发器，比如你想要每天定时3点发送一份统计邮箱，Trigger将会设置3点进行执行该任务。
  >
  > Trigger主要包含两种SimpleTrigger和CronTrigger两种。

- 调度器Scheduler

  > Scheduler为任务的调度器，它会将任务job以及触发器Trigger整合起来，负责基于Trigger设定时间来执行job。

## 3. quartz体系结构

Scheduler整合Trigger和Job之后，控制start， stop， pause， resume......

## 4. quartz常用API

- Scheduler用于与调度程序交互的主程序接口；

  Scheduler调度程序-任务执行计划表，只有安排进执行计划的任务job（通过scheduler.schedulerJob方法安排进执行计划），当他预先定义的执行时间到了的时候（任务触发Trigger），该任务才会执行；

- Job我们预先定义的希望在未来时间能被调度执行的任务类，我们可以自定义；

- JobDetail使用JobDetail来定义定时任务的实例，JobDetail实例是通过JobBuilder类创建的；

- JobDataMap可以包含不限量的（序列化）数据对象，在job实例执行的时候，可以使用其中的数据；JobDataMap是Java Map接口的一个实现，额外增加了一些便于存取的基本数据类型的数据的方法；

- Trigger触发器，Trigger对象是用来触发执行Job的。当调度一个job时，我们实例一个触发器然后调整他的属性来满足job执行的条件。表明任务在什么时候会执行。定义了一个已经被安排的任务将会在什么时候执行的时间条件，比如每2秒就执行一次。

- JobBuilder-用于声明一个任务实例，也可以定义关于该任务的详情比如任务名，组名等，这个声明的实例将会作为一个实际执行的任务；

- TriggerBuilder触发器创建器，用于创建触发器trigger实例；

- JobListener、TriggerListener、SchedulerListener监听器，用于对组件的监听。

## 5. quartz的使用

#### quartz依赖：

```
		<!-- quartz -->
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz</artifactId>
            <version>2.3.2</version>
        </dependency>
        <!-- quartz-jobs -->
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz-jobs</artifactId>
            <version>2.3.2</version>
        </dependency>
```

#### slf4j依赖：

```
		<!-- slf4j -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.12</version>
        </dependency>
        <!-- log4j -->
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>
```

#### log4j.properties

```
### direct log messages to stdout ###
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionoPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n

### direct messages to file mylog.log ###
log4j.appender.file=org.apache.log4j.FileAppender
log4j.appender.file.File=d:/mylog.log
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionoPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n

### set log levels - for more verbose logging change 'info' to 'debug' ###

log4j.rootLogger=info, stdout
```

### 5.1 quartz-helloworld模式

创建一个定时任务类：HelloJob

```
package com.zfl19.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 19zfl
 * @date 2023/5/15
 */
@Slf4j
public class HelloJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 输出当前时间
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(date);
        log.info(format);
    }

}
```

创建一个调度器：HelloScheduler

```
package com.zfl19.quartz.main;

import com.zfl19.quartz.job.HelloJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author 19zfl
 * @date 2023/5/15
 */

public class HelloScheduler {

    public static void main(String[] args) throws Exception {

        // 调度器（Scheduler），从工厂中获取调度实例
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        // 任务实例（JobDetail）
        JobDetail jobDetail = JobBuilder.newJob(HelloJob.class) // 加载任务类，与HelloJob完成绑定，要求重写org.quartz.Job类的execute方法
                .withIdentity("job1", "job-group1") // 参数1:任务的名称（唯一标识），参数2：任务组的名称
                .build();
        // 触发器（Trigger）
        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "trigger-group1") // 参数1:触发器的名称（唯一标识），参数2：触发器组的名称
                .startNow() // 马上启动
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatSecondlyForever(5))  // 每5秒重复执行一次
                .build();
        // 让调度器关联任务和触发器，保证按照触发器定义的条件执行任务
        scheduler.scheduleJob(jobDetail, trigger);
        // 启动
        scheduler.start();
    }

}
```

#### 小结：

>HelloWorld模式需要：
>
>- 任务Job类：你需要执行的任务；
>- Scheduler：任务调度器（让你的job程序按照你想的时间等条件运行起来的工具）：
>  - 从StdSchedulerFactory中获取调度器对象（实例）；
>  - 从JobBuilder获取任务实例，与任务Job类完成绑定（Job类必须完成重写org.quartz.Job这个类的execute方法；
>  - 从TriggerBuilder获取触发器对象，可一个设置任务Job类触发的时间等信息；
>  - 让调度器实例关联任务实例与触发器实例，保证按照触发器定义的条件执行任务；

### 5.2 Job和JobDetail介绍

- Job：工作任务调度接口，任务类需要实现该接口。该接口中定义execute方法，类似jdk提供的TimeTask类的run方法。在里面编写任务执行的业务逻辑代码；

- Job实例再Quartz中的生命周期：每次调度器执行Job时，他在调用execute方法前会创建一个新的Job实例，当调度完成后，关联的Job对象实例会被释放，释放的实例会被垃圾回收机制回收；

  ```
  // 当我在任务Job类中创建一个无参构造输出一句话
  @Slf4j
  public class HelloJob implements Job {
  
      public HelloJob() {
          System.out.println("任务被触发：");
      }
  
      @Override
      public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
          // 输出当前时间
          Date date = new Date();
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          String format = simpleDateFormat.format(date);
          log.info(format);
      }
  
  }
  ```

![Snipaste_2023-05-16_10-45-02](https://github.com/19zfl/quartz-demo/assets/130368992/ded66bf5-9e0d-4538-be5d-3173af575089)

足以证明：调度器执行任务Job类之后，该实例会被释放，下次执行需要重新创建新的实例。

- JobDetail：JobDetail为Job实例提供了许多设置属性，以及JobDataMap成员变量属性，它用来存储特定Job实例的状态信息，调度器需要借助JobDetail对象来添加Job实例；

- JobDetail重要属性：name，group，jobClass，jobDataMap

  ```
  // 任务实例（JobDetail）
          JobDetail jobDetail = JobBuilder.newJob(HelloJob.class) // 加载任务类，与HelloJob完成绑定，要求重写org.quartz.Job类的execute方法
                  .withIdentity("job1", "job-group1") // 参数1:任务的名称（唯一标识），参数2：任务组的名称
                  .build();
          String group = jobDetail.getKey().getGroup();
          String name = jobDetail.getKey().getName();
          log.info(group);
          log.info(name);
  ```

  ![Snipaste_2023-05-16_11-43-38](https://github.com/19zfl/quartz-demo/assets/130368992/c6347e06-9c1c-4566-a361-db3b585e499f)

<font color=red>注意：</font>

- 通过jobDetail.getJobClass.getName()获取与任务实例绑定的任务Job类名称;
- 当不给任务实例设置组名称group时，会使用quartz默认值：DEFAULT;
- 任务实例的任务名是必须要指定的，因为withIdentity方法中默认必须指定任务名

### 5.3 JobExecutionContext介绍

- 当Scheduler调用一个Job，就会将JobExecutionContext传递给Job的execute()方法；
- Job能通过JobExecution对象访问到Quartz运行时候的环境以及Job本身的明细数据；

```
@Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 获取jobDetail内容
        JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
        String name = jobKey.getName();     // 获取任务实例名称
        String group = jobKey.getGroup();   // 获取任务实例组名称
        log.info(name);
        log.info(group);
        String className = jobExecutionContext.getJobDetail().getJobClass().getName();
        String simpleClassName = jobExecutionContext.getJobDetail().getJobClass().getSimpleName();
        log.info(className);    // 获取任务实例绑定的任务Job类（全限定名形式）
        log.info(simpleClassName);  // 获取任务实例绑定的任务Job类（类名形式）
    }
```

![Snipaste_2023-05-16_12-27-30](https://github.com/19zfl/quartz-demo/assets/130368992/8ceeaf47-6c06-4bd5-b0e5-d1f5ca2c8f7b)

![Snipaste_2023-05-16_12-29-02](https://github.com/19zfl/quartz-demo/assets/130368992/1436f7c6-3a62-4881-8fd3-f45f46973b04)

也就是说：任务Job类与调度器中的任务实例JobDetail完成绑定之后，是能够通过org.quartz.Job类的execute方法中JobExecutionContext对象获取到任务实例的信息，当然在任务Job类中获取调度器中触发器实例Trigger信息也是跟获取任务实例JobDetail一致

### 5.4 JobDataMap介绍

（1）使用Map获取

- 在进行任务调度时，JobDataMap存储在JobExecutionContext中，非常方便获取；
- JobDataMap可以用来装载任何可以序列化的数据对象，当Job实例对象被执行时，这些对象会传递给他；
- JobDataMap实现了jdk的Map接口，并且添加非常方便的方法存取基本数据类型；

现在有一个需求，**我需要在任务Job类中获取到调度器中存入JobDataMap中的数据**：

HelloScheduler.java

```
@Slf4j
public class HelloScheduler {

    public static void main(String[] args) throws Exception {
        // 调度器（Scheduler），从工厂中获取调度实例
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        // 任务实例（JobDetail）
        JobDetail jobDetail = JobBuilder.newJob(HelloJob.class) // 加载任务类，与HelloJob完成绑定，要求重写org.quartz.Job类的execute方法
                .withIdentity("job1", "job-group1") // 参数1:任务的名称（唯一标识），参数2：任务组的名称
                .usingJobData("message", "data01")
                .build();
        // 触发器（Trigger）
        SimpleTrigger trigger = TriggerBuilder.newTrigger()
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
```

可以看到，**在调度器中任务实例和触发器实例都存入一个key为message，value为data的数据，在任务Job类中如何取值呢？**

HelloJob.java

```
@Slf4j
public class HelloJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 获取JobDataMap信息
        String messageByJobDetail = jobExecutionContext.getJobDetail().getJobDataMap().getString("message");
        String messageByTrigger = jobExecutionContext.getTrigger().getJobDataMap().getString("message");
        log.info(messageByJobDetail);
        log.info(messageByTrigger);
        // 输出当前时间
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(date);
        log.info(format);
    }
```

![Snipaste_2023-05-16_13-32-49](https://github.com/19zfl/quartz-demo/assets/130368992/ac38fbad-a055-4e4b-b122-1c4614a50610)

#### 常用方法：

- 获取当前任务Job类的执行时间

  ```
  @Override
      public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
          // 获取当前Job的执行时间
          Date fireTime = jobExecutionContext.getFireTime();
          SimpleDateFormat simpleDateFormatJob = new SimpleDateFormat();
          String formatDate = simpleDateFormatJob.format(fireTime);
          log.info(formatDate);
      }
  ```

- 获取下次任务Job类的执行时间

  ```
  @Override
      public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
          // 获取下次任务Job类的执行时间
          Date nextFireTime = jobExecutionContext.getNextFireTime();
          SimpleDateFormat simpleDateFormatJob = new SimpleDateFormat();
          String formatDate = simpleDateFormatJob.format(nextFireTime);
          log.info(formatDate);
      }
  ```

(2) Job实现类中添加setter方法对应JobDataMap，Quartz框架默认的JobFactory实现类在初始化Job实例对象时会自动的调用这些类setter方法。

在任务Job类中声明一个String类型的字段，之前在任务实例对象和触发器实例对象中存入的key为message，所以在任务类中声明一个String类型的字段message，提供setter方法即可；

<font color=red>注意：</font>

如果遇到同名的key，Trigger中的.usingJobData("message", "data01")会覆盖掉JobDetail中的.usingJobData("message", "data01");

### 5.5 有状态的Job和无状态的Job

@PersistJobDataAfterExecution注解的使用

有状态的Job可以理解为多次Job调用期间可以持有一些状态信息，这些状态信息存储在JobDataMap中，而默认的无状态Job每次调用时都会创建一个新的JobDataMap。

在quartz中声明的任务Job类是默认无状态的，每次调用执行都会创建一个新的实例，执行结束被收回；

HelloJob.java

```
@Slf4j
public class HelloJob implements Job {

    private Integer count;

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        ++count;
        log.info(count.toString());

        jobExecutionContext.getJobDetail().getJobDataMap().put("count", count);
    }

}
```

输出结果为：

![Snipaste_2023-05-16_15-40-05](https://github.com/19zfl/quartz-demo/assets/130368992/dadefa21-102a-4431-9def-030251bac0ce)

当在HelloJob类打上注解后：

![Snipaste_2023-05-16_15-38-46](https://github.com/19zfl/quartz-demo/assets/130368992/4de49e4e-9c49-4382-859e-29d812315c29)

HelloJob类上没有添加注解@PersisitJobDataAfterExecution，每次调用都会创建一个新的JobDataMap。不会累加；

HelloJob类添加@PersisitJobDataAfterExecution注解后，多次调用Job期间可以持有一些状态信息，即可以实现count的累加操作。

### 5.6 Trigger触发器介绍

通过TriggerBuilder得到Tirgger实例

Trigger：

- AbsolutelyTrigger
  - DailyTimeIntervalTriggerImpl
  - CalendarIntervalTriggerImpl
  - SimpleTriggerImpl
  - CronTriggerImpl

Quartz有一些不同的触发器类型，不过，用的最多的是SimpleTrigger和CronTrigger

（1）jobKey

表示job实例的标识，触发器被触发时，该指定的job实例挥别执行；

（2）startTime

表示触发器的时间表，第一次开始被触发的时间，它的数据类型是java.util.Date；

（3）endTime

指定触发器终止被触发的时间，它的数据类型是java.util.Date；

JobSimpleTrigger.java

```JobSimpleTrigger.java
@PersistJobDataAfterExecution
@Slf4j
public class JobSimpleTrigger implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 输出当前时间
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(date);
        log.info("now time : " + format);
    }

}
```

JobSchedulerTrigger.java

```JobSchedulerTrigger.java
@Slf4j
public class JobSchedulerTrigger {

    public static void main(String[] args) throws Exception {

        // 调度器（Scheduler），从工厂中获取调度实例
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        // 任务实例（JobDetail）
        JobDetail jobDetail = JobBuilder.newJob(JobSimpleTrigger.class) // 加载任务类，与HelloJob完成绑定，要求重写org.quartz.Job类的execute方法
                .withIdentity("job1", "job-group1") // 参数1:任务的名称（唯一标识），参数2：任务组的名称
                .usingJobData("message", "data01")
                .build();

        // 触发器（Trigger）
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "trigger-group1") // 参数1:触发器的名称（唯一标识），参数2：触发器组的名称
//                .startNow() // 马上启动
                .build();
        // 让调度器关联任务和触发器，保证按照触发器定义的条件执行任务
        scheduler.scheduleJob(jobDetail, trigger);
        // 启动
        scheduler.start();
    }

}
```

需要注意的是，如果没有指定时间循环条件的话，使用startNow()方法，则任务类Job只会执行一次，如果我们在任务Job类中想要获取Trigger实例的startTime和endTime的时候会出现**空指针异常**，此时我们需要配置以下：（不需要立即执行，而是我们自己设置开始和结束时间）

JobSchedulerTrigger.java

```JobSchedulerTrigger.java
@Slf4j
public class JobSchedulerTrigger {

    public static void main(String[] args) throws Exception {

        // 调度器（Scheduler），从工厂中获取调度实例
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        Date startDate = new Date(); // 开始执行时间
        Date endDate = new Date(); // 结束执行时间
        endDate.setTime(endDate.getTime() + 10000); // 开始执行时间10秒后结束
        // 任务实例（JobDetail）
        JobDetail jobDetail = JobBuilder.newJob(JobSimpleTrigger.class) // 加载任务类，与HelloJob完成绑定，要求重写org.quartz.Job类的execute方法
                .withIdentity("job1", "job-group1") // 参数1:任务的名称（唯一标识），参数2：任务组的名称
                .usingJobData("message", "data01")
                .build();

        // 触发器（Trigger）
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "trigger-group1") // 参数1:触发器的名称（唯一标识），参数2：触发器组的名称
//                .startNow() // 马上启动
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatSecondlyForever(5))
                .startAt(startDate)
                .endAt(endDate)
                .build();
        // 让调度器关联任务和触发器，保证按照触发器定义的条件执行任务
        scheduler.scheduleJob(jobDetail, trigger);
        // 启动
        scheduler.start();
    }

}
```

![Snipaste_2023-05-16_16-16-33](https://github.com/19zfl/quartz-demo/assets/130368992/459232a2-00c2-4970-9dbb-d944760b668b)

详细说明：通过执行输出看出，我们设置了重复5秒执行一次，因为我们设置了触发器的开始执行和结束执行时间在10秒内，所以任务Job类执行的次数为2次。

### 5.7 SimpleTrigger触发器

SimplTrigger对于设置和使用是最为简单的一种QuartzTrigger。

它是为那种需要在特定的日期/时间启动，且以一个可能的间隔重复执行n次的Job所设计的。

案例1：表示在一个特定的时间段内，执行一次任务：

案例2：在指定的时间间隔内多次执行任务：

案例3：指定任务执行时间：

<font color=red>需要注意：</font>

- SimpleTrigger的属性有：开始时间，结束时间，重复次数和重复的时间间隔；
- 重复次数属性的值可以为0，正整数，或常量SimpleTrigger.REPEAT_INDEFINITELY。
- 重复的时间间隔属性值必须为0或长整型的正整数，以毫秒作为单位，当重复的时间间隔为0时，意味着与Trigger同时出发执行。
- 如果有指定结束时间属性值，则结束时间属性优先于重复次数属性，这样子的好处在于：当我们需要创建一个每间隔10秒触发一次直到指定的结束时间的Trigger，而无需去计算从开始到结束的所重复的次数，我们只需要简单的指定结束时间和使用REPEAT_INDEFINITELY作为重复次数的属性值即可；

### 5.8 CronTrigger触发器

CronTrigger是基于日历的作业调度器

（1）Cron Expressions——Cron表达式

Cron表达式被用来配置CronTrigger实例。Cron表达式是一个由7个子表达式组成的字符串。每个子表达式都描述了一个单独的日程细节。这些子表达式用空格分隔，分别表示：

- Seconds：秒
- Minutes：分钟
- Hours：小时
- Day-of-Month：月中的天
- Month：月
- Day-of-Week：周中的天
- Year(optional field)：年（可选的域）

取值：

| 字段 | 是否必填 | 允许值                | 运行的特殊字符  |
| ---- | -------- | --------------------- | --------------- |
| 秒   | 是       | 0-59                  | ，- * /         |
| 分   | 是       | 0-59                  | ，- * /         |
| 小时 | 是       | 0-23                  | ，- * /         |
| 日   | 是       | 1-31                  | ，- * / ？L W C |
| 月   | 是       | 1-12或者JAN-DEC       | ，- * /         |
| 周   | 是       | 1-7或者SUN-SAT        | ，- * / ？L C # |
| 年   | 否       | 不填写，或者1970-2099 | ，- * /         |

![Snipaste_2023-05-16_17-43-51](https://github.com/19zfl/quartz-demo/assets/130368992/cecea6cc-c0dc-4202-b4f4-000bda3089fc)

![Snipaste_2023-05-16_17-52-35](https://github.com/19zfl/quartz-demo/assets/130368992/ef03449e-2777-4366-b190-8cb47e18c6d6)

提示：

- “L”和“W”可以一起使用。（企业可用在工资计算）
- “#”可表示月中第几个周几。（企业中可用在计算母亲节和父亲节）
- 周字段英文字母不区分大小写，例如：MON = mon。
- 利用工具，在线生成。

### 5.9 配置，资源SchedulerFactory

Quartz以模块方式架构，因此，要使它运行，几个组件必须很好的咬合在一起。幸运的是，已经有了一些现存的助手可以完成这些工作。

所有的Scheduler实例有SchedulerFactory创建

Quartz的三个核心概念：调度器，任务，触发器，三者之间关系是：

![Snipaste_2023-05-16_18-04-33](https://github.com/19zfl/quartz-demo/assets/130368992/54e63bb8-c737-4255-b0e2-f52bb34757e0)

一个作业，比较重要的就是Scheduler，jobDetail，Trigger；

对于job而言就好比一个驱动器，没有触发器来定时驱动作业。作业就无法运行；对于Job而言，一个job可以对应多个Trigger，但对于Trigger而言，一个Trigger只能对应一个Job，所以一个Trigger只能被指派给一个Job；如果你需要一个更复杂的触发计划，可以创建多个Trigger并指派他们给同一个Job。

Scheduler的创建方式：

（1）StdSchedulerFactoru：

Quartz默认的SchedulerFactory

- 使用一组参数（java.util.Properties）来创建和初始化Quartz调度器

- 配置参数一般存储在quartz.properties文件中

- 调用getScheduler方法就能创建和初始化调度器对象

  ```
  StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
  Scheduler scheduler = stdSchedulerFactory.getScheduler();
  ```

用法1：输出调度器开始的时间（重要：使得任务和触发器产生关联）

```
Date date = scheduler.scheduleJob(jobDetail, trigger);
SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
String format = simpleDateFormat.format(date);
log.info(format);
```

用法2：启动任务调度

`scheduler.start();`

用法3：任务调度挂起，任务暂停

```
		// 执行两秒后自动挂起
        Thread.sleep(2000L);
        // 挂起
        scheduler.standby();
        // 执行5秒后自动开启
        Thread.sleep(5000L);
        // 启动
        scheduler.start();
```

用法4：关闭任务调度

shutdown()

shutdown(true)：表示等待所有正在执行的Job执行完毕之后，再关闭Scheduler；

shutdown(false)：表示直接关闭Scheduler

（2）DirectSchedulerFactory（了解）：

DirectSchedulerFactory是对SchedulerFactory的直接实现，通过它可以直接构建Schedulee，thread pool等；

```
DirectSchedulerFactory instance = DirectSchedulerFactory.getInstance();
Scheduler scheduler = instance.getScheduler();
```

### 5.10 Quartz.properties

默认路径：quartz-2.3.0中的org.quartz中的quartz.properties

![Snipaste_2023-05-16_19-12-21](https://github.com/19zfl/quartz-demo/assets/130368992/da20e556-f471-40d7-b3cc-b69954a897af)

我们将复制一份在resource文件夹中新建一个同名quartz.properties的文件，更改配置：org.quartz.threadPool.threadCount: -1

再次运行任务调度器会报错：

![Snipaste_2023-05-16_19-17-06](https://github.com/19zfl/quartz-demo/assets/130368992/b275de04-b2b4-4620-b6b6-c1a23fba56ea)

可以知道，如果我们在resource资源文件夹中存在一个同名文件，quartz默认会使用resource文件夹下的配置文件。

更多配置信息参考：https://blog.csdn.net/qq_39669058/article/details/90443857

使用配置工厂临时配置

QuartzProperties.java

```
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
```

不出意外启动会报错如图：

![Snipaste_2023-05-16_19-38-24](https://github.com/19zfl/quartz-demo/assets/130368992/4b1aed80-2411-4b38-91b7-b10598a1c447)

这就说明，我们通过配置工厂进行临时配置quartz.properties文件是有效果的。

### 5.11 Quartz监听器

（1）概念

Quartz 的监听器用于当任务调座中你所关注事件发生时 ， 能够及时获取这一事亻牛的通知 。 类似于亻壬务执行过程中的邮件 、 短信类的提醒。 Quartz 监听器主要有 JobListener 、 TriggerListener 、 SchedulerListener 三种 ， 顾名思义 ， 分别表示任务 、 触发器 、 调度器对应的监听器 。 三者的使用方法类似 ， 在开始介绍三种监听器之前需要明确两个概念 ： 全局监听器与非全局监听器 ， 二者的区别在于 ：
全局监听器能够接收到所有的job/Trigger的事件通知 ，
而非全局监听器只能接收到在其上注册的job或Trigger的事件 ， 不在其上注册的job或Trigger则不会进行监听 。

（2）JobListener

任务调度过程中 ，与任务 Job相关的事件包括 ：Job开始要执行的提示；Job执行完成的提示；
