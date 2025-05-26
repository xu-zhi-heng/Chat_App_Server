package com.sweetfun.server;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 这个是通过IP来判断
public class ChannelManage {
    public static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static final Map<String, Channel> ipChannelMap = new ConcurrentHashMap<>();

    public static void addChannel(String ip, Channel channel) {
        channels.add(channel);
        ipChannelMap.put(ip, channel);
    }

    public static void removeChannel(Channel channel) {
        channels.remove(channel);
        ipChannelMap.values().removeIf(c -> c.id().equals(channel.id()));
    }

    public static Channel getChannelByIp(String ip) {
        return ipChannelMap.get(ip);
    }

}
