package com.sweetfun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sweetfun.domain.Message;
import com.sweetfun.domain.vo.FriendMessageVo;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService extends IService<Message> {
    List<Message> getMessageBetweenUsers(Long userId1, Long userId2, LocalDateTime time);
    List<FriendMessageVo> getMessageList(Long userId);
}
