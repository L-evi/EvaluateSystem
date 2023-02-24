package com.project.evaluate.constant;

import sun.jvm.hotspot.utilities.AssertionFailure;

public class ThreadPoolConstant {
    /**
     * 核心线程数量
     */
    public static final int CORE_THREAD_NUM = 10;

    /**
     * 最大线程数量
     */
    public static final int MAX_THREAD_NUM = 15;

    /**
     * 非核心线程活动时间
     */
    public static final long KEEP_ALIVE_TIME_SECONDS = 1L;

    /**
     * 任务队列长度
     */
    public static final int QUEUE_LENGTH = 8;

    /**
     * 线程超时时间
     */
    public static final long THREAD_TIME_OUT = 70;

    private ThreadPoolConstant() {
        throw new AssertionFailure(ThreadPoolConstant.class.getName() + ": ERROR!");
    }

}
