package com.sweetfun.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public interface WebSocketFrameHandler {
    boolean support(Class<? extends WebSocketFrame> frameType);
    void handle(ChannelHandlerContext ctx, WebSocketFrame frame);
}
