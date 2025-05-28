package com.sweetfun.controller;

import com.sweetfun.annotation.RequireToken;
import com.sweetfun.domain.Friend;
import com.sweetfun.domain.vo.FriendListVo;
import com.sweetfun.response.Result;
import com.sweetfun.service.FriendService;
import com.sweetfun.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friend")
@Slf4j
public class FriendController {

    @Autowired
    private FriendService friendService;

    @PostMapping("/addFriend")
    @RequireToken
    public Result<?> addFriend(@RequestParam Long friendId) {
        Long userId = UserContext.getUserId();
        if (userId == null || friendId == null || userId.equals(friendId)) {
            return Result.error(400, "用户ID或者好友ID无效");
        }
        return friendService.addFriend(userId, friendId);
    }

    @RequestMapping("/confirmFriend")
    @RequireToken
    public Result<?> confirmFriend(@RequestParam Long friendId) {
        Long userId = UserContext.getUserId();
        if (userId == null || friendId == null || userId.equals(friendId)) {
            return Result.error(400, "用户ID或者好友ID无效");
        }
        return friendService.confirmFriend(userId, friendId);
    }

    @GetMapping("/getFriendList")
    @RequireToken
    public Result<?> getFriendList(@RequestParam Byte status) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(400, "用户ID为空");
        }
        List<FriendListVo> friendList = friendService.getFriendList(userId, status);
        if (friendList != null) {
            return Result.success(friendList, "查询好友列表成功");
        } else {
            return Result.success(null, "查询好友列表失败");
        }
    }

}
