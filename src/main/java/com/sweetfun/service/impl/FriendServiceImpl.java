package com.sweetfun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sweetfun.domain.Friend;
import com.sweetfun.domain.User;
import com.sweetfun.domain.vo.FriendListVo;
import com.sweetfun.mapper.FriendMapper;
import com.sweetfun.response.Result;
import com.sweetfun.service.FriendService;
import com.sweetfun.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    @Autowired
    private UserService userService;

    @Override
    public Result<?> addFriend(Long userId, Long friendId) {
        QueryWrapper<Friend> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("friend_id", friendId);
        try {
            Friend existing  = this.getOne(wrapper);
            if (existing  != null) {
                if (existing .getStatus() == 1) {
                    return Result.success(true, "对方已经是你的好友了");
                } else if (existing .getStatus() == 2) {
                    return Result.success(true, "已发送过添加请求");
                }
            }
        } catch (Exception exception) {
            log.error("查询好友关系失败", exception);
            return Result.error(500, "查询好友关系失败");
        }
        // 查询对方是否发送过好友请求
        QueryWrapper<Friend> reverseWrapper = new QueryWrapper<>();
        reverseWrapper.eq("user_id", friendId).eq("friend_id", userId);
        Friend reverse = this.getOne(reverseWrapper);
        if (reverse != null) {
            return Result.success(true, "对方已向你发送过好友请求, 请同意");
        }
        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(friendId);
        friend.setStatus((byte) 2);
        try {
            boolean save = this.save(friend);
            if (save) {
                return Result.success(true, "发送添加好友请求成功");
            } else {
                return Result.error(500, "发送添加好友请求失败");
            }
        } catch (Exception exception) {
            log.error("添加好友失败", exception);
            return Result.error(500, "发送添加好友请求失败");
        }
    }


    @Override
    public Result<?> confirmFriend(Long userId, Long friendId) {
        try {
            QueryWrapper<Friend> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", friendId).eq("friend_id", userId).eq("status", 2);
            Friend request = this.getOne(wrapper);
            if (request == null) {
                return Result.error(400, "未找到好友请求");
            }
            request.setStatus((byte) 1);
            request.setUpdateTime(LocalDateTime.now().withNano(0));
            this.updateById(request);

            // 反向查找 user ➝ requester 是否已存在记录
            QueryWrapper<Friend> reverseWrapper = new QueryWrapper<>();
            reverseWrapper.eq("user_id", userId).eq("friend_id", friendId);
            Friend reverse = this.getOne(reverseWrapper);
            if (reverse != null) {
                reverse.setStatus((byte) 1);
                reverse.setUpdateTime(LocalDateTime.now());
                this.updateById(reverse);
            } else {
                Friend newFriend = new Friend();
                newFriend.setUserId(userId);
                newFriend.setFriendId(friendId);
                newFriend.setStatus((byte) 1);
                this.save(newFriend);
            }
            return Result.success(true, "好友请求已同意");
        } catch (Exception exception) {
            log.error("确认好友请求失败, {}", exception.getMessage());
            return Result.error(500, "确认失败，请稍后重试");
        }
    }

    @Override
    public List<FriendListVo> getFriendList(Long userId, Byte status) {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("status", status);
        try {
            List<Friend> friendList = this.list(queryWrapper);
            if (friendList == null || friendList.isEmpty()) {
                return Collections.emptyList();
            }
            List<FriendListVo> friendListVos = new ArrayList<>();
            friendList.forEach(friend -> {
                User friendInfo = userService.getById(friend.getFriendId());
                FriendListVo friendListVo = new FriendListVo();
                BeanUtils.copyProperties(friend, friendListVo, FriendListVo.class);
                BeanUtils.copyProperties(friendInfo, friendListVo, "id");
                friendListVos.add(friendListVo);
            });
            return friendListVos;
        } catch (Exception exception) {
            log.error("查询好友列表失败", exception);
            return null;
        }
    }

}
