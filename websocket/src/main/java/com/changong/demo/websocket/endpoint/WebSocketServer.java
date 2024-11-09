package com.changong.demo.websocket.endpoint;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.changong.demo.websocket.cache.WsMemoryCache;
import com.changong.demo.websocket.dto.WsMessage;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * 地图 WebSocket 服务
 *
 * @author 张赛超
 * @version 1.0.0
 * @since 2024/11/9
 */
@ServerEndpoint("/websocket/{username}")
@Component
@Slf4j
public class WebSocketServer {
    /** 用于控制并发访问 */
    private final Object lock = new Object();


    /**
     * 发送消息给指定的会话。
     *
     * @param session 会话对象
     * @param message 消息内容
     * @throws IOException 发送消息失败时抛出异常
     */
    public void sendMessage(Session session, String message) throws IOException {
        if (session == null || !session.isOpen()) {
            log.warn("会话已关闭或不存在，无法发送消息：{}", message);
            return;
        }

        synchronized (lock) {
            try {
                log.info("发送数据为：{}", message);
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("发送消息失败：{}", message, e);
                throw e;
            }
        }
    }


    /**
     * 给指定用户发送信息。
     *
     * @param username 用户名
     * @param message 消息内容
     */
    public void sendInfo(String username, String message) {
        Session session = WsMemoryCache.getSession(username);
        try {
            sendMessage(session, message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * 群发消息给所有在线用户。
     *
     * @param message 消息内容
     */
    public void broadcast(String message) {
        Map<String, Session> sessions = WsMemoryCache.getAllSessions();
        for (Map.Entry<String, Session> entry : sessions.entrySet()) {
            try {
                sendMessage(entry.getValue(), message);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }


    /**
     * 建立连接成功时调用。
     *
     * @param session 会话对象
     * @param username 用户名
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "username") @NotNull String username) {
        WsMemoryCache.addSession(username, session);
        WsMemoryCache.incrementOnlineCount();
        log.info("用户：{} 连接到 WebSocket！当前连接人数为：{}", username, WsMemoryCache.getOnlineCount());

        // 广播上线消息
        WsMessage message = new WsMessage(username, null, username + " 上线", new Date());
        broadcast(JSONUtil.toJsonStr(message));
    }


    /**
     * 关闭连接时调用。
     *
     * @param username 用户名
     */
    @OnClose
    public void onClose(@PathParam(value = "username") @NotNull String username) {
        WsMemoryCache.removeSession(username);
        WsMemoryCache.decrementOnlineCount();
        log.info("用户：{} 断开 WebSocket 连接！当前连接人数为：{}", username, WsMemoryCache.getOnlineCount());

        // 广播下线消息
        WsMessage message = new WsMessage(username, null, username + " 下线", new Date());
        broadcast(JSONUtil.toJsonStr(message));
    }


    /**
     * 收到客户端信息后，根据接收人的用户名广播消息或单发消息。
     *
     * @param message 客户端发送的消息
     * @throws IOException 发送消息失败时抛出异常
     */
    @OnMessage
    public void onMessage(String message) throws IOException {
        log.info("服务接收到客户端消息：{}", message);
        WsMessage msg = JSONUtil.toBean(message, WsMessage.class);
        msg.setTimestamp(new Date());

        if (StrUtil.isEmpty(msg.getRecipientUserName())) {
            broadcast(JSONUtil.toJsonStr(msg));
        } else {
            sendInfo(msg.getRecipientUserName(), JSONUtil.toJsonStr(msg));
        }
    }


    /**
     * 错误时调用。
     *
     * @param session 会话对象
     * @param throwable 异常对象
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("WebSocket 发生错误！异常信息：{}", throwable.getMessage());
    }
}
