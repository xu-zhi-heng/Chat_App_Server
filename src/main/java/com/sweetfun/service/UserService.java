package com.sweetfun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sweetfun.domain.User;

public interface UserService extends IService<User> {
    String login(String username, String password, String ip);
    String register(User user);
}
