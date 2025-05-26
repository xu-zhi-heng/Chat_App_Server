package com.sweetfun.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sweetfun.config.MsgTypeDeserializer;
import com.sweetfun.emun.MsgType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@TableName("message")
@Data
public class Message {
    @TableId
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    @JsonDeserialize(using = MsgTypeDeserializer.class)
    private MsgType msgType;
    private Integer status;
    @DateTimeFormat
    private LocalDateTime createTime;
}
