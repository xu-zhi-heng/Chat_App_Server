package com.sweetfun.controller;

import com.sweetfun.annotation.RequireToken;
import com.sweetfun.domain.Message;
import com.sweetfun.domain.vo.FriendMessageVo;
import com.sweetfun.response.Result;
import com.sweetfun.service.MessageService;
import com.sweetfun.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/message")
@Slf4j
public class MessageController {
    @Autowired
    private MessageService messageService;

    // 根据用户之间的对话信息
    @GetMapping("/getMessagesBetweenUsers")
    @RequireToken
    public Result<?> getMessagesBetweenUsers(@RequestParam Long userId1,
                                             @RequestParam Long userId2,
                                             @RequestParam Long time) {
        LocalDateTime localTime = Instant.ofEpochMilli(time)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .truncatedTo(ChronoUnit.SECONDS);
        List<Message> messages = messageService.getMessageBetweenUsers(userId1, userId2, localTime, false);
        if (messages != null) {
            return Result.success(messages, "查询对话信息成功");
        } else {
            return Result.error(500, "查询对话信息失败");
        }
    }


    @PostMapping("/updateMessage")
    @RequireToken
    public Result<?> updateMessage(@RequestBody List<Message> messages) {
        if (messages.size() == 0) {
            log.warn("更新messages数据为null");
            return Result.error(500, "请求数据为null");
        }
        boolean isUpdate = messageService.updateMessage(messages);
        if (isUpdate) {
            return Result.success(true, "更新消息成功");
        } else {
            return Result.error(500, "更新消息失败");
        }
    }

    // 这个暂时用不到，之前是因为没有想创建聊天列表这个表，所以想动态计算
    @GetMapping("/getMessageList")
    @RequireToken
    public Result<?> getMessage() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(400, "用户ID无效");
        }
        List<FriendMessageVo> messageList = messageService.getMessageList(userId);
        if (messageList == null) {
            return Result.error(500, "查询消息列表失败");
        } else {
            return Result.success(messageList, "查询消息列表成功");
        }
    }
}
