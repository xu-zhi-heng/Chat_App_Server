package com.sweetfun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sweetfun.domain.Friend;
import com.sweetfun.response.Result;

public interface FriendService extends IService<Friend> {
    Result<?> addFriend(Long userId, Long friendId);
    Result<?> getFriendList(Long userId, Byte status);
    Result<?> confirmFriend(Long userId, Long friendId);
}
