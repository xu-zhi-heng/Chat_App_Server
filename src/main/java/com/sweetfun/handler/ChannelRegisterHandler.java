package com.sweetfun.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sweetfun.domain.Message;
import com.sweetfun.emun.MsgType;
import com.sweetfun.server.UserChannelManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ChannelRegisterHandler implements MessageHandler{

    private final UserChannelManager userChannelManager;
    private final ObjectMapper objectMapper;

    public ChannelRegisterHandler(UserChannelManager userChannelManager, ObjectMapper objectMapper) {
        this.userChannelManager = userChannelManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean support(MsgType msgType) {
        return MsgType.Auth.equals(msgType);
    }

    @Override
    public void handle(Message message, ChannelHandlerContext ctx) throws JsonProcessingException {
        try {
            Long senderId = message.getSenderId();
            userChannelManager.register(senderId.toString(), ctx.channel());
            ObjectNode registerMessage = objectMapper.createObjectNode();
            Message reply = new Message();
            reply.setMsgType(MsgType.Auth);
            reply.setContent(MsgType.Auth.getDesc());
            reply.setCreateTime(LocalDateTime.now().withNano(0));
            reply.setSenderId(senderId);
            ctx.writeAndFlush((new TextWebSocketFrame(objectMapper.writeValueAsString(reply))));
        } catch (Exception exception) {
            log.error("消息通道注册失败, {}", exception.getMessage());
            // 向客户端返回错误响应
            ObjectNode errorMsg = objectMapper.createObjectNode();
            errorMsg.put("code", 500);
            errorMsg.put("desc", "消息通道注册失败: " + exception.getMessage());
            ctx.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(errorMsg)));
        }
    }
}
