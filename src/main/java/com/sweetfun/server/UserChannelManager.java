package com.sweetfun.server;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 这个是通过UserId来判断
@Component
public class UserChannelManager {

    private final Map<String, Channel> userChannelMap = new ConcurrentHashMap<>();

    public void register(String userId, Channel channel) {
        userChannelMap.put(userId, channel);
    }

    public void remove(String userId) {
        userChannelMap.remove(userId);
    }

    public Channel getChannel(String userId) {
        return userChannelMap.get(userId);
    }

}
