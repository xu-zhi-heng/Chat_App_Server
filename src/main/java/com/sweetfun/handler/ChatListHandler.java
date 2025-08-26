package com.sweetfun.handler;

import com.sweetfun.domain.ChatList;
import com.sweetfun.service.ChatListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ChatListHandler {

    @Autowired
    private ChatListService chatListService;

    /**
     * 确保用户对话列表正确更新：
     * - 发送方：对话列表肯定存在，只更新lastMessageId
     * - 接收方：需要判断消息列表是否存在，没有就创建，如果之前被删除过，那么就恢复，并且更新未读数和lastMessageId
     */
    public void ensureChatListEntry(Long senderId, Long receiverId, Long messageId) {
        // 发送方：只维护 lastMessageId，不增加未读
        handleChatListEntryForSender(senderId, receiverId, messageId);
        // 接收方：维护 lastMessageId，并处理存在性、恢复和未读计数
        handleChatListEntryForReceiver(receiverId, senderId, messageId);
    }

    private void handleChatListEntryForSender(Long userId, Long friendId, Long messageId) {
        // 发送方对话列表肯定存在，直接查询获取
        ChatList chat = chatListService.findChatList(userId, friendId);

        // 如果之前被删除，恢复状态, , 兜底使用
        if (chat.getIsDeleted() == 1) {
            chat.setIsDeleted(0);
            chat.setIsPinned(0);
            chat.setUnreadCount(0L); // 发送方未读始终为0
            chat.setCreateTime(LocalDateTime.now().withNano(0));
        }

        // 更新最后一条消息ID
        chat.setLastMessageId(messageId);
        chatListService.updateChatList(chat);
    }

    private void handleChatListEntryForReceiver(Long userId, Long friendId, Long messageId) {
        ChatList chat = chatListService.findChatList(userId, friendId);

        if (chat == null) {
            // 不存在则创建新的对话列表
            createNewChatList(userId, friendId, messageId);
            return;
        }

        if (chat.getIsDeleted() == 1) {
            // 已删除则恢复
            restoreDeletedChat(chat);
        } else {
            // 未删除则增加未读计数
            chat.setUnreadCount(chat.getUnreadCount() + 1);
        }

        // 更新最后一条消息ID
        chat.setLastMessageId(messageId);
        chatListService.updateChatList(chat);
    }

    private void createNewChatList(Long userId, Long friendId, Long messageId) {
        ChatList chatList = new ChatList();
        chatList.setUserId(userId);
        chatList.setFriendId(friendId);
        chatList.setUnreadCount(1L); // 新建时，接收方未读为1
        chatList.setIsDeleted(0);
        chatList.setIsPinned(0);
        chatList.setCreateTime(LocalDateTime.now().withNano(0));
        chatList.setLastMessageId(messageId);
        chatListService.createChatList(chatList);
    }

    private void restoreDeletedChat(ChatList chat) {
        chat.setIsDeleted(0);
        chat.setIsPinned(0);
        chat.setUnreadCount(1L); // 恢复时计为1条未读
        chat.setCreateTime(LocalDateTime.now().withNano(0));
    }

}