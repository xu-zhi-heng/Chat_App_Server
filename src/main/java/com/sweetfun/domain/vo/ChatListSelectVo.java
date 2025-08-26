package com.sweetfun.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sweetfun.domain.ChatList;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ChatListSelectVo extends ChatList {
    private String avatar;
    private String nickName;
    private String content;
    private Long unreadCount;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageTime;
}
