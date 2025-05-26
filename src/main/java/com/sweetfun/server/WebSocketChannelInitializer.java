package com.sweetfun.server;

import com.sweetfun.handler.AuthHandler;
import com.sweetfun.handler.ChatHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final ChatHandler chatHandler;
    private final AuthHandler authHandler;

    @Autowired
    public WebSocketChannelInitializer(ChatHandler chatHandler, AuthHandler authHandler) {
        this.chatHandler = chatHandler;
        this.authHandler = authHandler;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerProtocolHandler("/chat"));
        // 先进行注册在进行聊天消息发送
        pipeline.addLast(authHandler);
        pipeline.addLast(chatHandler);
    }
}
