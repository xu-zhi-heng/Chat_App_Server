package com.sweetfun.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sweetfun.domain.Message;
import com.sweetfun.emun.MsgType;
import com.sweetfun.server.UserChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 用来处理用户当前发送消息的状态
@Component
@Slf4j
public class TypingStatusHandler implements MessageHandler {

    private final UserChannelManager userChannelManager;
    private final ObjectMapper objectMapper;

    public TypingStatusHandler(UserChannelManager userChannelManager, ObjectMapper objectMapper) {
        this.userChannelManager = userChannelManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean support(MsgType msgType) {
        return ( msgType.equals(MsgType.START_TYPING) || msgType.equals(MsgType.END_TYPING) );
    }

    @Override
    public void handle(Message message, ChannelHandlerContext ctx) throws JsonProcessingException {
        try {
            Long receiverId = message.getReceiverId();
            Channel channel = userChannelManager.getChannel(receiverId.toString());
            if (channel == null) {
                log.info("{}用户不在线", receiverId);
            } else {
                log.info("channel是否活跃: {}", channel.isActive());
                Message reply = new Message();
                reply.setMsgType(message.getMsgType());
                reply.setSenderId(message.getSenderId());
                reply.setReceiverId(receiverId);
                reply.setContent(message.getMsgType().getDesc());
                channel.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(reply)));
            }
        } catch (Exception exception) {
            log.error("用户发送消息状态处理失败, {}", exception.getMessage());
            ObjectNode errorMsg = objectMapper.createObjectNode();
            errorMsg.put("code", 500);
            errorMsg.put("desc", "用户发送消息状态处理失败: " + exception.getMessage());
            ctx.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(errorMsg)));
        }
    }
}
