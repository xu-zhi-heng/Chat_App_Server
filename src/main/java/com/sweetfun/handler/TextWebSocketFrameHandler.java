package com.sweetfun.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetfun.domain.Message;
import com.sweetfun.emun.MsgType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TextWebSocketFrameHandler implements WebSocketFrameHandler {

    private final ObjectMapper objectMapper;

    private final Map<MsgType, MessageHandler> handlerMap; // 用Map缓存

    @Autowired
    public TextWebSocketFrameHandler(ObjectMapper objectMapper,
                                     List<MessageHandler> messageHandlerList) {
        this.objectMapper = objectMapper;
        // 初始化Map，支持一个处理器处理多种消息类型
        this.handlerMap = initializeHandlerMap(messageHandlerList);
    }

    /**
     * 初始化处理器映射，将每个处理器支持的所有消息类型都加入映射
     */
    private Map<MsgType, MessageHandler> initializeHandlerMap(List<MessageHandler> messageHandlerList) {
        Map<MsgType, MessageHandler> map = new HashMap<>();

        for (MessageHandler handler : messageHandlerList) {
            boolean hasSupportedType = false;

            // 遍历所有所有消息类型，将支持的类型都添加到映射中
            for (MsgType type : MsgType.values()) {
                if (handler.support(type)) {
                    map.put(type, handler);
                    hasSupportedType = true;
                }
            }

            // 检查当前处理器器是否至少支持一种消息类型
            if (!hasSupportedType) {
                throw new IllegalArgumentException("Handler不支持任何MsgType: " + handler.getClass());
            }
        }

        return map;
    }

    @Override
    public boolean support(Class<? extends WebSocketFrame> frameType) {
        return TextWebSocketFrame.class.isAssignableFrom(frameType);
    }

    @Override
    public void handle(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) {
        // todo 后期的消息会先放入MQ，然后异步的存入到mysql中, 还有ACK确定机制
        String msg = ((TextWebSocketFrame) webSocketFrame).text();
        try {
            Message message = objectMapper.readValue(msg, Message.class);
            if (message != null) {
                MessageHandler handler = handlerMap.get(message.getMsgType());
                if (handler != null) {
                    handler.handle(message, ctx);
                } else {
                    log.warn("未找到支持的处理器, msgType: {}", message.getMsgType());
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("消息处理失败: " + e.getMessage(), e);
        }
    }
}
