package com.sweetfun.domain.vo;

import com.sweetfun.emun.MsgType;
import lombok.Data;

@Data
public class FriendMessageVo extends FriendListVo{
    private String content;
    private MsgType msgType;
}
