package com.sweetfun.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebSocketServer {
    private static final int port = 8082;

    private final WebSocketChannelInitializer webSocketChannelInitializer;

    @Autowired
    public WebSocketServer(WebSocketChannelInitializer webSocketChannelInitializer) {
        this.webSocketChannelInitializer = webSocketChannelInitializer;
    }

    public void start() throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(webSocketChannelInitializer);
        ChannelFuture future = bootstrap.bind(port).sync();
        System.out.println("Netty WebSocket 服务器启动成功，端口：8082");
        future.channel().closeFuture().sync();
    }
}
