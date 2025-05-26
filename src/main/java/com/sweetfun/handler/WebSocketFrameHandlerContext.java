package com.sweetfun.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class WebSocketFrameHandlerContext {

    private final List<WebSocketFrameHandler> handlers;

    public WebSocketFrameHandlerContext(List<WebSocketFrameHandler> handlers) {
        this.handlers = handlers;
    }

    public void handle(ChannelHandlerContext ctx, WebSocketFrame frame) {
        for (WebSocketFrameHandler handler : handlers) {
            if (handler.support(frame.getClass())) {
                handler.handle(ctx, frame);
                return;
            }
        }
        log.warn("未找到适配的处理器：{}", frame.getClass().getSimpleName());
    }


}
