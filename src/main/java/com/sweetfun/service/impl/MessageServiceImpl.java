package com.sweetfun.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sweetfun.domain.Friend;
import com.sweetfun.domain.Message;
import com.sweetfun.mapper.MessageMapper;
import com.sweetfun.response.Result;
import com.sweetfun.service.FriendService;
import com.sweetfun.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public List<Message> getMessageBetweenUsers(Long userId1, Long userId2, LocalDateTime time) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        // 构造 ((userId1 = A and userId2 = B) or (userId1 = B and userId2 = A))
        wrapper.nested(w -> {
            w.eq("sender_id", userId1).eq("receiver_id", userId2).or()
                    .eq("sender_id", userId2).eq("receiver_id", userId1);
        });
        // 时间小于指定时间
        wrapper.lt("create_time", time);
        // 按时间倒序, limit 20
        wrapper.orderByDesc("create_time").last("limit 20");
        return this.list(wrapper);
    }

    @Override
    public Result<?> getMessageList(Long userId) {
        return null;
    }


}
