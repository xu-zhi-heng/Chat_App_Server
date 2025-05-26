package com.sweetfun.controller;

import com.sweetfun.annotation.RequireToken;
import com.sweetfun.domain.Message;
import com.sweetfun.response.Result;
import com.sweetfun.service.MessageService;
import com.sweetfun.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    // 根据用户之间的对话信息
    @GetMapping("/getMessagesBetweenUsers")
    @RequireToken
    public List<Message> getMessagesBetweenUsers(@RequestParam Long userId1,
                                             @RequestParam Long userId2,
                                             @RequestParam LocalDateTime time) {
        return messageService.getMessageBetweenUsers(userId1, userId2, time);
    }

    @GetMapping("/getMessageList")
    @RequireToken
    public Result<?> getMessage() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(400, "用户ID无效");
        }
        return messageService.getMessageList(userId);
    }


}
