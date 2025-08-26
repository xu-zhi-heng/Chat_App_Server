package com.sweetfun.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetfun.domain.Message;
import com.sweetfun.server.UserChannelManager;
import com.sweetfun.service.MessageService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TextMessageHandler implements WebSocketFrameHandler{

    private final ObjectMapper objectMapper;
    private final UserChannelManager userChannelManager;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatListHandler chatListHandler;

    @Autowired
    public TextMessageHandler(ObjectMapper objectMapper, UserChannelManager userChannelManager) {
        this.objectMapper = objectMapper;
        this.userChannelManager = userChannelManager;
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
            message.setStatus(0);
            messageService.save(message);
            // 检查消息列表
            chatListHandler.ensureChatListEntry(message.getSenderId(), message.getReceiverId(), message.getId());
            Long receiverId = message.getReceiverId();
            Channel channel = userChannelManager.getChannel(receiverId.toString());
            log.info("获取到channel为: {}", channel);
            if (channel == null) {
                log.info("{}用户不在线", receiverId);
                // 设置消息为未读状态
                message.setStatus(0);
                messageService.updateById(message);
            } else {
                log.info("channel是否活跃: {}", channel.isActive());
                channel.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(message)));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
