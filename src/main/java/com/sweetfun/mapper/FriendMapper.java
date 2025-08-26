package com.sweetfun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sweetfun.domain.Friend;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface FriendMapper extends BaseMapper<Friend> {

}
