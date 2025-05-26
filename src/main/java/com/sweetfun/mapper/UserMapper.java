package com.sweetfun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sweetfun.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
}
