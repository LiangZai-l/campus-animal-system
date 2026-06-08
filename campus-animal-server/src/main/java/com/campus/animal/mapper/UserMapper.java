package com.campus.animal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.animal.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
