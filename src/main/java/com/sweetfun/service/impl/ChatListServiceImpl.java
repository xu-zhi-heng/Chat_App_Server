package com.sweetfun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sweetfun.domain.ChatList;
import com.sweetfun.domain.Message;
import com.sweetfun.domain.User;
import com.sweetfun.domain.vo.ChatListSelectVo;
import com.sweetfun.emun.MsgType;
import com.sweetfun.mapper.ChatListMapper;
import com.sweetfun.service.ChatListService;
import com.sweetfun.service.MessageService;
import com.sweetfun.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatListServiceImpl extends ServiceImpl<ChatListMapper, ChatList> implements ChatListService {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @Override
    public List<ChatListSelectVo> getAllChatListByUserId(Long userId) {
        QueryWrapper<ChatList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("is_deleted", 0);
        queryWrapper.orderByDesc("update_time");
        try {
            List<ChatList> chatLists = this.list(queryWrapper);
            if (chatLists.isEmpty()) {
                return Collections.emptyList();
            }
            List<ChatListSelectVo> result = chatLists.stream()
                    .map(chatList -> {
                        ChatListSelectVo vo = new ChatListSelectVo();
                        BeanUtils.copyProperties(chatList, vo);
                        Long friendId = chatList.getFriendId();
                        User friend = userService.getById(friendId);
                        vo.setAvatar(friend.getAvatar());
                        vo.setNickName(friend.getNickname());
                        if (chatList.getLastMessageId() != null) {
                            Message message = messageService.getById(chatList.getLastMessageId());
                            if (message != null) {
                                if (message.getMsgType() == null) {
                                    vo.setContent(MsgType.UNKNOWN.getDesc());
                                } else {
                                    switch (message.getMsgType()) {
                                        case TEXT -> vo.setContent(message.getContent());
                                        case IMAGE, VIDEO, VOICE, REFERENCE -> vo.setContent(message.getMsgType().getDesc());
                                        default -> vo.setContent(MsgType.UNKNOWN.getDesc());
                                    }
                                }
                                vo.setLastMessageTime(message.getCreateTime());
                            }
                        }
                        return vo;
                    })
                    .collect(Collectors.toList());
            return result;
        } catch (Exception exception) {
            log.error("根据用户id查询消息列表错误: {}", exception.getMessage());
            return null;
        }
    }

    @Override
    public boolean deleteChatList(Long id) {
        ChatList chatList = new ChatList();
        chatList.setId(id);
        chatList.setIsDeleted(1);
        try {
            return this.updateById(chatList);
        } catch (Exception exception) {
            log.error("删除消息列表失败, {}", exception.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateChatList(ChatList chatList) {
        try {
            return this.updateById(chatList);
        } catch (Exception exception) {
            log.error("更新消息列表失败, {}", exception.getMessage());
            return false;
        }
    }

    @Override
    public ChatList createChatList(ChatList chatList) {
        try {
            // 先查询是否存在未删除的记录
            QueryWrapper<ChatList> activeQuery = new QueryWrapper<>();
            activeQuery.eq("user_id", chatList.getUserId());
            activeQuery.eq("friend_id", chatList.getFriendId());
            activeQuery.eq("is_deleted", 0);
            ChatList activeChat = this.getOne(activeQuery);

            if (activeChat != null) {
                log.warn("该消息列表已经存在且未被删除，ID: {}", activeChat.getId());
                return activeChat;
            }

            // 检查是否存在已删除的记录
            QueryWrapper<ChatList> deletedQuery = new QueryWrapper<>();
            deletedQuery.eq("user_id", chatList.getUserId());
            deletedQuery.eq("friend_id", chatList.getFriendId());
            deletedQuery.eq("is_deleted", 1);
            ChatList deletedChat = this.getOne(deletedQuery);

            if (deletedChat != null) {
                // 恢复已删除的记录
                deletedChat.setIsDeleted(0);
                deletedChat.setUnreadCount(chatList.getUnreadCount());
                deletedChat.setIsPinned(chatList.getIsPinned());
                deletedChat.setLastMessageId(chatList.getLastMessageId());
                deletedChat.setCreateTime(chatList.getCreateTime());
                deletedChat.setUpdateTime(LocalDateTime.now().withNano(0));

                boolean updateResult = this.updateById(deletedChat);
                if (updateResult) {
                    log.info("已恢复被删除的消息列表，ID: {}", deletedChat.getId());
                    return deletedChat;
                } else {
                    log.error("恢复被删除的消息列表失败");
                    return null;
                }
            }

            // 既没有未删除的也没有已删除的，创建新记录
            boolean saveResult = this.save(chatList);
            if (saveResult) {
                log.info("创建新消息列表成功，ID: {}", chatList.getId());
                return chatList;
            } else {
                log.error("保存消息列表失败，返回值为false");
                return null;
            }
        } catch (Exception exception) {
            log.error("处理消息列表失败, {}", exception.getMessage());
            return null;
        }
    }

    @Override
    public ChatList findChatList(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            log.warn("userId 和 friendId 不能为空");
            return null;
        }
        QueryWrapper<ChatList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("friend_id", friendId);
        try {
            return this.getOne(queryWrapper);
        } catch (Exception exception) {
            log.error("查询消息列表失败, {}", exception.getMessage());
            return null;
        }
    }

}
