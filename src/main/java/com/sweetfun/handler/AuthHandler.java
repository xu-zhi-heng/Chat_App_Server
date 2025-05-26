package com.sweetfun.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetfun.server.UserChannelManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
@Slf4j
public class AuthHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private final UserChannelManager userChannelManager;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthHandler(UserChannelManager userChannelManager, ObjectMapper objectMapper) {
        this.userChannelManager = userChannelManager;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) throws Exception {
        if (webSocketFrame instanceof TextWebSocketFrame) {
            String msg = ((TextWebSocketFrame) webSocketFrame).text();
            JsonNode json  = objectMapper.readTree(msg);
            if ("auth".equals(json.get("msgType").asText())) {
                String userId = json.get("userId").asText();
                userChannelManager.register(userId, ctx.channel());
                // 告诉客户端连接成功
                ctx.writeAndFlush(new TextWebSocketFrame("认证成功"));
                return;
            }
            // TextWebSocketFrame 是引用计数对象（Netty 的 ByteBuf 派生类），
            // 如果你想将消息传递下去，一定要加 .retain()，否则消息会被释放，后续 Handler 会报错。
            ctx.fireChannelRead(webSocketFrame.retain());
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        log.info("退出了");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        log.info("添加了");
    }
}
