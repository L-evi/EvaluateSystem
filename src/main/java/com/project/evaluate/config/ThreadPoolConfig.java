package com.project.evaluate.config;

import com.project.evaluate.constant.ThreadPoolConstant;

import java.util.concurrent.*;

/**
 * 线程池配置
 * 学习链接：https://blog.csdn.net/Xin_101/article/details/121567666
 */
public class ThreadPoolConfig {
    public static ExecutorService threadPoolExecutorGenerate = new ThreadPoolExecutor(
            ThreadPoolConstant.CORE_THREAD_NUM,
            ThreadPoolConstant.MAX_THREAD_NUM,
            ThreadPoolConstant.KEEP_ALIVE_TIME_SECONDS,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(ThreadPoolConstant.QUEUE_LENGTH),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );
}
