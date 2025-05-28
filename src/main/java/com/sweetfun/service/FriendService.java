package com.sweetfun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sweetfun.domain.Friend;
import com.sweetfun.domain.vo.FriendListVo;
import com.sweetfun.response.Result;

import java.util.List;

public interface FriendService extends IService<Friend> {
    Result<?> addFriend(Long userId, Long friendId);
    List<FriendListVo> getFriendList(Long userId, Byte status);
    Result<?> confirmFriend(Long userId, Long friendId);
}
