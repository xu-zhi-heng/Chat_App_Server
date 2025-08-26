package com.sweetfun.controller;

import com.sweetfun.annotation.RequireToken;
import com.sweetfun.domain.User;
import com.sweetfun.emun.ResultCode;
import com.sweetfun.response.Result;
import com.sweetfun.service.UserService;
import com.sweetfun.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> loginInfo, HttpServletRequest request) {
        String username = loginInfo.get("username");
        String password = loginInfo.get("password");
        if (username == null || password == null) {
            return Result.error(ResultCode.BAD_REQUEST.getCode(), "用户名或密码不能为空");
        }
        String ip = request.getRemoteAddr();
        try {
            String token = userService.login(username, password, ip);
            if (token == null) {
                return Result.error(500, "账号或密码错误");
            } else {
                return Result.success(token, "登录成功");
            }
        } catch (RuntimeException e) {
            return Result.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        try {
            String userName = userService.register(user);
            // 字符常量放在前面防止 NullPointerException
            if (!"".equals(userName)) {
                return Result.success(userName, "注册成功");
            } else {
                return Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "注册失败");
            }
        } catch (RuntimeException e) {
            return Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @RequestMapping("/profile")
    @RequireToken
    public Result<User> getUserInfo() {
        Long userId = UserContext.getUserId();
        User user = userService.getById(userId);
        return Result.success(user);
    }

    @RequestMapping("/update")
    @RequireToken
    public Result<?> updateUser(@RequestBody User user) {
        Long userId = UserContext.getUserId();
        user.setId(userId);
        boolean isUpdate = userService.updateById(user);
        return Result.success(isUpdate);
    }

}













