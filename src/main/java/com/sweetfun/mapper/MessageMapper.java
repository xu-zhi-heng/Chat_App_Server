package com.sweetfun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sweetfun.domain.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    List<Message> getLastMessagesWithFriends(@Param("friendIds") List<Long> friendIds, @Param("userId") Long userId);
}
