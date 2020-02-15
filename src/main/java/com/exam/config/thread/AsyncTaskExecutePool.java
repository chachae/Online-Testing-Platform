package com.exam.config.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务线程池装配类
 *
 * @author yzn
 * @see <a href="https://juejin.im/entry/5abb8f6951882555677e9da2"/>
 * @date 2020/1/29 13:22
 */
@Slf4j
@Configuration
public class AsyncTaskExecutePool implements AsyncConfigurer {

  /** 注入配置类 */
  private final TaskExecutionProperties props;

  public AsyncTaskExecutePool(TaskExecutionProperties config) {
    this.props = config;
  }

  @Override
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    // 核心线程池大小
    executor.setCorePoolSize(props.getPool().getCoreSize());
    // 最大线程数
    executor.setMaxPoolSize(props.getPool().getMaxSize());
    // 队列容量
    executor.setQueueCapacity(props.getPool().getQueueCapacity());
    // 活跃时间
    executor.setKeepAliveSeconds((int) props.getPool().getKeepAlive().getSeconds());
    // 线程名字前缀
    executor.setThreadNamePrefix("el-async-");
    // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
    // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return (throwable, method, objects) -> {
      log.error("====" + throwable.getMessage() + "====", throwable);
      log.error("exception method:" + method.getName());
    };
  }
}
