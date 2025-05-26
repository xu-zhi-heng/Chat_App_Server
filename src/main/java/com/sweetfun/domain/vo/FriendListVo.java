package com.sweetfun.domain.vo;

import com.sweetfun.domain.Friend;
import lombok.Data;

@Data
public class FriendListVo extends Friend {
    private String avatar;
    private Byte isOnline;
    private String nickname;
    private String signature;
}
