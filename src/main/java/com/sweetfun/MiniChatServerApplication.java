package com.sweetfun;

import com.sweetfun.server.WebSocketServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.sweetfun.mapper")
@SpringBootApplication
public class MiniChatServerApplication implements CommandLineRunner {

    @Autowired
    private WebSocketServer webSocketServer;

    public static void main(String[] args) {
        SpringApplication.run(MiniChatServerApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        webSocketServer.start();
    }


}
