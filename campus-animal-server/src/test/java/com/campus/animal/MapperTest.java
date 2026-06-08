package com.campus.animal;

import com.campus.animal.entity.User;
import com.campus.animal.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testQueryUsers() {
        List<User> users = userMapper.selectList(null);
        assertNotNull(users);
        assertFalse(users.isEmpty(), "数据库应至少有一条用户记录（初始化管理员）");
        users.forEach(u -> {
            assertNotNull(u.getUsername());
            System.out.println("用户: " + u.getUsername() + ", 角色: " + u.getRole());
        });
    }
}
