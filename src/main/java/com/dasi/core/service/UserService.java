package com.dasi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dasi.common.result.PageResult;
import com.dasi.pojo.dto.PasswordDTO;
import com.dasi.pojo.dto.StatusDTO;
import com.dasi.pojo.dto.LoginDTO;
import com.dasi.pojo.dto.UserPageDTO;
import com.dasi.pojo.entity.User;
import com.dasi.pojo.vo.UserLoginVO;
import com.dasi.pojo.vo.UserPageVO;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService extends IService<User> {
    void register(LoginDTO loginDTO);

    UserLoginVO login(LoginDTO loginDTO);

    String refresh(HttpServletRequest request);

    PageResult<UserPageVO> getUsers(UserPageDTO userPageDTO);

    void updateStatus(StatusDTO statusDTO);

    void updatePassword(PasswordDTO passwordDTO);

    void removeUser(Long id);
}
