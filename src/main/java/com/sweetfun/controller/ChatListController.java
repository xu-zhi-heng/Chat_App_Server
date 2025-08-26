package com.sweetfun.controller;

import com.sweetfun.annotation.RequireToken;
import com.sweetfun.domain.ChatList;
import com.sweetfun.domain.vo.ChatListSelectVo;
import com.sweetfun.response.Result;
import com.sweetfun.service.ChatListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatList")
@Slf4j
public class ChatListController {

    @Autowired
    private ChatListService chatListService;

    @GetMapping("/getChatList")
    @RequireToken
    public Result<?> getAllChatListByUserId(@RequestParam Long userId) {
        if (userId == null) {
            log.warn("用户id为空");
            return Result.error(500, "用户id为空");
        }
        List<ChatListSelectVo> allChatListByUserId = chatListService.getAllChatListByUserId(userId);
        if (allChatListByUserId == null) {
            return Result.error(500, "查询消息列表失败");
        } else {
            return Result.success(allChatListByUserId, "查询消息列表成功");
        }
    }


    @PostMapping("/deleteChatList")
    @RequireToken
    public Result<?> deleteChatList(@RequestParam Long id) {
        if (id == null) {
            log.warn("用户id为空");
            return Result.error(500, "用户id为空");
        }
        boolean deleteChatList = chatListService.deleteChatList(id);
        if (deleteChatList) {
            return Result.success(true, "删除消息列表成功");
        } else {
            return Result.error(500, "删除消息列表失败");
        }
    }

    @PostMapping("updateChatList")
    @RequireToken
    public Result<?> updateChatList(@RequestBody ChatList chatList) {
        if (chatList == null) {
            log.warn("更新chatList数据为null");
            return Result.error(500, "请求数据为null");
        }
        boolean b = chatListService.updateChatList(chatList);
        if (b) {
            return Result.success(true, "更新消息列表成功");
        } else {
            return Result.error(500, "更新消息列表失败");
        }
    }

    @PostMapping("/createChatList")
    @RequireToken
    public Result<?> createChatList(@RequestBody ChatList chatList) {
        if (chatList == null) {
            log.warn("创建chatList数据为null");
            return Result.error(500, "请求数据为null");
        }
        ChatList chat = chatListService.createChatList(chatList);
        if (chat != null) {
            return Result.success(chat, "创建消息列表成功");
        } else {
            return Result.error(500, "创建消息列表失败");
        }
    }
}
