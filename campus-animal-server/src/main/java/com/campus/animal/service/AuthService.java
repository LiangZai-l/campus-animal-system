package com.campus.animal.service;

import com.campus.animal.dto.LoginDTO;
import com.campus.animal.dto.RegisterDTO;
import com.campus.animal.vo.LoginVO;
import com.campus.animal.vo.UserVO;

public interface AuthService {
    void register(RegisterDTO registerDTO);
    LoginVO login(LoginDTO loginDTO);
    UserVO getCurrentUserInfo();
}
