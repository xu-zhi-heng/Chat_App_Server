package com.sweetfun.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sweetfun.domain.Message;
import com.sweetfun.emun.MsgType;
import io.netty.channel.ChannelHandlerContext;

public interface MessageHandler {
    boolean support(MsgType msgType);
    void handle(Message message, ChannelHandlerContext ctx) throws JsonProcessingException;
}
