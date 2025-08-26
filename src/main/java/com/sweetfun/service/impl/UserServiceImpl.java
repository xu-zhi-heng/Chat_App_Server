package com.sweetfun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sweetfun.domain.User;
import com.sweetfun.mapper.UserMapper;
import com.sweetfun.service.UserService;
import com.sweetfun.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public String login(String username, String rawPassword, String ip) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        try {
            User user = this.getOne(wrapper);
            if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
                log.error("{}, 验证失败", username);
                throw new RuntimeException("用户名或密码错误");
            }
            user.setLoginIp(ip);
            user.setLastLoginTime(LocalDateTime.now().withNano(0));
            user.setIsOnline((byte) 1);
            this.updateById(user);
            return jwtUtils.generateToken(user.getId(), user.getUsername());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    @Override
    public String register(User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getPhone, user.getPhone()).or().eq(User::getEmail, user.getEmail());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("手机号或邮箱已存在");
        }
        String username;
        do {
            username = String.valueOf((long) (Math.random() * 9_000_000_000L + 1_000_000_000L));
        } while (this.count(new QueryWrapper<User>().eq("username", username)) > 0);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setIsOnline((byte) 0);
        boolean isSaveSuccess = this.save(user);
        return isSaveSuccess ? username : "";
    }
}
