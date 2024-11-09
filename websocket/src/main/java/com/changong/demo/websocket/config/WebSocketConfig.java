package com.changong.demo.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类，用于注册和管理WebSocket端点。
 * 该配置类通过Spring容器创建并管理{@link ServerEndpointExporter} bean，
 * 允许应用程序中的WebSocket端点（标记了@ServerEndpoint注解的类）被自动发现和初始化。
 *
 * @author 张赛超
 * @version 1.0.0
 * @since 2024/11/9
 */
@Configuration
public class WebSocketConfig {

    /**
     * 创建一个ServerEndpointExporter bean，它是Spring框架与Java API for WebSocket (JSR 356)之间的桥梁。
     * 通过此bean，可以将使用@ServerEndpoint注解标记的类暴露为WebSocket端点。
     *
     * @return ServerEndpointExporter对象，用于处理WebSocket端点的注册。
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
