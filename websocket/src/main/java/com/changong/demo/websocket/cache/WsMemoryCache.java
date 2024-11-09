package com.changong.demo.websocket.cache;

import jakarta.websocket.Session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket 会话缓存管理类，使用内存存储会话信息。
 * 不能使用 Redis，应为 Session 无法被反序列化
 *
 * TODO 对于分布式部署，需要通过中间件实现 Session 同步机制。可以通过监听通道中的消息流，从本地读取 SessionPool，然后判断有无此用户 Session，进行简单的过滤。有则处理，无则丢弃
 * TODO 若是进一步优化，可借助 MQ 或 Redis 等记录 SessionId 和对应服务，来快速找到对应 Session
 *
 * @author 张赛超
 * @version 1.0.0
 * @since 2024/11/9
 */
public class WsMemoryCache {

    /** 静态变量，用来记录当前在线连接数 */
    private static final AtomicInteger ONLINE_NUM = new AtomicInteger(0);

    /** 线程安全 Map，用来存放每个客户端对应的 WebSocketServer 对象 */
    private static final ConcurrentHashMap<String, Session> SESSION_MAP = new ConcurrentHashMap<>();


    /**
     * 增加在线连接数。
     */
    public static void incrementOnlineCount() {
        ONLINE_NUM.incrementAndGet();
    }


    /**
     * 减少在线连接数。
     */
    public static void decrementOnlineCount() {
        ONLINE_NUM.decrementAndGet();
    }


    /**
     * 获取当前在线连接数。
     *
     * @return 当前在线连接数
     */
    public static int getOnlineCount() {
        return ONLINE_NUM.get();
    }


    /**
     * 添加一个会话到会话池。
     *
     * @param username 用户名
     * @param session 会话对象
     */
    public static void addSession(String username, Session session) {
        SESSION_MAP.put(username, session);
    }


    /**
     * 从会话池中移除一个会话。
     *
     * @param username 用户名
     */
    public static void removeSession(String username) {
        SESSION_MAP.remove(username);
    }


    /**
     * 获取会话池中的会话对象。
     *
     * @param username 用户名
     * @return 会话对象
     */
    public static Session getSession(String username) {
        return SESSION_MAP.get(username);
    }


    /**
     * 获取会话池中的所有会话对象。
     *
     * @return 会话对象集合
     */
    public static ConcurrentHashMap<String, Session> getAllSessions() {
        return new ConcurrentHashMap<>(SESSION_MAP);
    }
}
