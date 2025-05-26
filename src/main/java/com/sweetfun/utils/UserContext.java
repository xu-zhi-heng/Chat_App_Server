package com.sweetfun.utils;

// 保存用户的信息
public class UserContext {

    /**
     * ThreadLocal 是每个线程一份副本，它的作用正是为了解决多线程之间共享变量时的数据隔离问题。
     * 在 Spring Boot 的 Web 应用中，每个 HTTP 请求会由一个独立的线程处理（来自线程池）。
     * 你可以 更改这个线程池的配置，甚至可以换成自己的线程池，但你不能完全取消“每个请求由一个线程”这个模型，
     * 因为这是 Servlet 规范的基本模型之一。
     *
     * 每个线程都会有自己独立的 ThreadLocal 副本。
     * 所以 不同用户的请求不会共享同一个 userId，它们存在于各自的线程上下文中。
     * 但是用完一定要清理，不然会出现内存泄漏
     *
     * 用户A发起请求 → 分配线程T1 → ThreadLocal.get() 返回A的 userId
     * 用户B发起请求 → 分配线程T2 → ThreadLocal.get() 返回B的 userId
     *
     * 不适用于异步线程场景：
     * 比如使用异步线程池执行任务（@Async）时 ThreadLocal 是不可见的；
     * 如果有异步业务，应该显式传递上下文。
     *
     */
    private static final ThreadLocal<Long> userHolder = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        userHolder.set(userId);
    }

    public static Long getUserId() {
        return userHolder.get();
    }

    public static void clear() {
        userHolder.remove();
    }
}
