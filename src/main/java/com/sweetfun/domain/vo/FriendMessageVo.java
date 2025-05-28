package com.sweetfun.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sweetfun.emun.MsgType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class FriendMessageVo extends FriendListVo {
    private String content;
    private MsgType msgType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime msgCreateTime;
}
