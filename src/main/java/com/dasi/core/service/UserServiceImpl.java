package com.dasi.core.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.enumeration.UserRole;
import com.dasi.common.exception.InboxErrorException;
import com.dasi.common.exception.LoginException;
import com.dasi.common.properties.JwtProperties;
import com.dasi.core.mapper.UserMapper;
import com.dasi.pojo.dto.UserDTO;
import com.dasi.pojo.entity.User;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.RegisterException;
import com.dasi.pojo.vo.UserVO;
import com.dasi.util.InboxUtil;
import com.dasi.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InboxUtil inboxUtil;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public void register(UserDTO userDTO) {
        // 1. 检查重名
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>().eq("username", userDTO.getUsername());
        User user = getOne(queryWrapper);
        if (user != null) {
            throw new RegisterException(ResultInfo.USER_ALREADY_EXISTS);
        }

        // 2. 构建 User
        user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(SecureUtil.md5(userDTO.getPassword()));
        user.setInbox(inboxUtil.nextId());
        user.setRole(UserRole.ADMIN);
        user.setStatus(1);

        // 3. 保存 User
        try {
            save(user);
        } catch (Exception e) {
            log.error("创建用户错误：{}", e.getMessage());
            throw new InboxErrorException(ResultInfo.USER_SAVE_ERROR);
        }

        log.debug("注册用户成功：{}", userDTO);
    }

    @Override
    public UserVO login(UserDTO userDTO) {
        // 1. 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>().eq("username", userDTO.getUsername());
        User user = getOne(queryWrapper);
        if (user == null) {
            throw new LoginException(ResultInfo.USER_NOT_EXIST);
        }

        // 2. 校验密码
        String password = SecureUtil.md5(userDTO.getPassword());
        if (!password.equals(user.getPassword())) {
            throw new LoginException(ResultInfo.PASSWORD_ERROR);
        }

        // 3. 检查账号状态
        if (user.getStatus() == 0) {
            throw new LoginException(ResultInfo.USER_DISABLED);
        }

        // 4. 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        String token = jwtUtil.createToken(claims);

        // 5. 构造视图
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        userVO.setToken(token);

        log.debug("登陆用户成功：{}", userDTO);
        return userVO;
    }

    @Override
    public String refresh(HttpServletRequest request) {
        String oldToken = request.getHeader(jwtProperties.getTokenName());
        String newToken = jwtUtil.refreshToken(oldToken);
        log.debug("刷新 Token 成功：{}", newToken);
        return newToken;
    }
}