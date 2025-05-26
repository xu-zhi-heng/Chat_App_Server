package com.sweetfun.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private final WebSocketFrameHandlerContext handlerContext;

    @Autowired
    public ChatHandler(WebSocketFrameHandlerContext handlerContext) {
        this.handlerContext = handlerContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) throws Exception {
        handlerContext.handle(ctx, webSocketFrame);
    }


}
