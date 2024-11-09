package com.changong.demo.websocket.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * WebSocket 消息 DTO
 *
 * 该类用于表示通过 WebSocket 发送和接收的消息。
 * 包含发送者、接收者、消息文本和发送时间等信息。
 *
 * @author 张赛超
 * @version 1.0.0
 * @since 2024/11/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 发送者的用户名
     */
    private String senderUserName;

    /**
     * 接收者的用户名
     */
    private String recipientUserName;

    /**
     * 消息内容
     */
    private String contentText;

    /**
     * 消息发送时间
     * 使用 {@link JSONField} 注解来指定日期格式
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;
}
