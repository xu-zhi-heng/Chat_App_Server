package com.sweetfun.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sweetfun.domain.Message;
import com.sweetfun.domain.vo.FriendListVo;
import com.sweetfun.domain.vo.FriendMessageVo;
import com.sweetfun.mapper.MessageMapper;
import com.sweetfun.service.FriendService;
import com.sweetfun.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private FriendService friendService;

    @Override
    public List<Message> getMessageBetweenUsers(Long userId1, Long userId2, LocalDateTime time, boolean isAll) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        // 构造 ((userId1 = A and userId2 = B) or (userId1 = B and userId2 = A))
        wrapper.nested(w -> {
            w.eq("sender_id", userId1).eq("receiver_id", userId2).or()
                    .eq("sender_id", userId2).eq("receiver_id", userId1);
        });
        // 时间小于指定时间
        if (time != null) {
            wrapper.lt("create_time", time);
        }
        if (!isAll) {
            // 取最新的前20条消息
            wrapper.orderByDesc("create_time").last("limit 20");
        }
        try {
            List<Message> messages = this.list(wrapper);
            if (messages.isEmpty()) {
                return Collections.emptyList();
            } else {
                Collections.reverse(messages);
                return messages;
            }
        } catch (Exception exception) {
            log.error("查询用户对话信息失败: {}", exception.getMessage());
            return null;
        }
    }

    @Override
    public List<FriendMessageVo> getMessageList(Long userId) {
        try {
            List<FriendListVo> friendListVos = friendService.getFriendList(userId, (byte) 1);
            if (CollectionUtils.isEmpty(friendListVos)) {
                return Collections.emptyList();
            }
            // 提取所有好友ID
            List<Long> friendIds = friendListVos.stream()
                    .map(FriendListVo::getFriendId)
                    .collect(Collectors.toList());
            // 查询与这些好友的最近消息
            List<Message> messages = messageMapper.getLastMessagesWithFriends(friendIds, userId);
            log.info("messages: {}", messages);
            if (CollectionUtils.isEmpty(messages)) {
                return Collections.emptyList();
            }
            // 将消息转为 Map<friendId, message>
            Map<Long, Message> latestMsgMap = messages.stream()
                    .collect(Collectors.toMap(
                            msg -> msg.getSenderId().equals(userId) ? msg.getReceiverId() : msg.getSenderId(),
                            msg -> msg
                    ));
            List<FriendMessageVo> resultList = friendListVos.stream()
                    .map(vo -> {
                        FriendMessageVo messageVo = new FriendMessageVo();
                        BeanUtils.copyProperties(vo, messageVo);
                        Message msg = latestMsgMap.get(vo.getFriendId());
                        if (msg != null) {
                            messageVo.setContent(msg.getContent());
                            messageVo.setMsgType(msg.getMsgType());
                            messageVo.setMsgCreateTime(msg.getCreateTime());
                        }
                        return messageVo;
                    })
                    .collect(Collectors.toList());
            return resultList;
        } catch (Exception exception) {
            log.error("查询信息列表失败, {}", exception.getMessage());
            return null;
        }
    }

    @Override
    public boolean updateMessage(List<Message> messages) {
        try {
            messages.removeIf(message -> message.getId() == null);
            return this.updateBatchById(messages);
        } catch (Exception exception) {
            log.error("更新消息失败, {}", exception.getMessage());
            return false;
        }
    }
}
