package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.pojo.dto.UserDTO;
import com.dasi.pojo.entity.User;
import com.dasi.pojo.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService extends IService<User> {
    void register(UserDTO userDTO);

    UserVO login(UserDTO userDTO);

    String refresh(HttpServletRequest request);
}
