package com.sweetfun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sweetfun.domain.ChatList;
import com.sweetfun.domain.vo.ChatListSelectVo;

import java.util.List;

public interface ChatListService extends IService<ChatList> {
    List<ChatListSelectVo> getAllChatListByUserId(Long userId);
    boolean deleteChatList(Long id);
    boolean updateChatList(ChatList chatList);
    ChatList createChatList(ChatList chatList);
    ChatList findChatList(Long userId, Long friendId);
}
