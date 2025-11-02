package com.dasi.core.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.SuperAdminOnly;
import com.dasi.common.constant.DefaultConstant;
import com.dasi.common.enumeration.UserRole;
import com.dasi.common.exception.*;
import com.dasi.common.properties.JwtProperties;
import com.dasi.common.result.PageResult;
import com.dasi.core.mapper.ContactMapper;
import com.dasi.core.mapper.UserMapper;
import com.dasi.pojo.dto.PasswordDTO;
import com.dasi.pojo.dto.StatusDTO;
import com.dasi.pojo.dto.LoginDTO;
import com.dasi.pojo.dto.UserPageDTO;
import com.dasi.pojo.entity.Contact;
import com.dasi.pojo.entity.User;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.pojo.vo.UserLoginVO;
import com.dasi.pojo.vo.UserPageVO;
import com.dasi.util.InboxUtil;
import com.dasi.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private InboxUtil inboxUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(LoginDTO loginDTO) {
        // 检查重名
        long count = count(new QueryWrapper<User>().eq("username", loginDTO.getUsername()));
        if (count > 0) {
            throw new RegisterException(ResultInfo.USER_ALREADY_EXIST);
        }

        // 构建用户
        User user = new User();
        user.setUsername(loginDTO.getUsername());
        user.setPassword(SecureUtil.md5(loginDTO.getPassword()));
        user.setRole(UserRole.ADMIN);
        user.setStatus(1);
        if (!save(user)) {
            throw new RegisterException(ResultInfo.USER_SAVE_ERROR);
        }

        // 构建联系人
        Contact contact = new Contact();
        contact.setName(user.getUsername());
        contact.setEmail(DefaultConstant.DEFAULT_EMAIL);
        contact.setInbox(inboxUtil.nextId());
        contact.setStatus(1);
        contactMapper.insert(contact);

        log.debug("【User Service】注册用户：{}", loginDTO);
    }

    @Override
    public UserLoginVO login(LoginDTO loginDTO) {
        // 1. 查询用户
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", loginDTO.getUsername()));
        if (user == null) {
            throw new LoginException(ResultInfo.USER_NOT_EXIST);
        }

        // 2. 校验密码
        String password = SecureUtil.md5(loginDTO.getPassword());
        if (!password.equals(user.getPassword())) {
            throw new LoginException(ResultInfo.USER_PASSWORD_ERROR);
        }

        // 3. 检查账号状态
        if (user.getStatus() == 0) {
            throw new LoginException(ResultInfo.USER_DISABLED);
        }

        // 4. 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put(jwtProperties.getClaimUserKey(), user.getId());
        String token = jwtUtil.createToken(claims);

        // 5. 构造视图
        UserLoginVO userLoginVO = BeanUtil.copyProperties(user, UserLoginVO.class);
        userLoginVO.setToken(token);

        log.debug("【User Service】用户登陆：{}", loginDTO);
        return userLoginVO;
    }

    @Override
    public String refresh(HttpServletRequest request) {
        String oldToken = request.getHeader(jwtProperties.getTokenName());
        String newToken = jwtUtil.refreshToken(oldToken);
        log.debug("【User Service】刷新 Token ：{}", newToken);
        return newToken;
    }

    @Override
    public PageResult<UserPageVO> getUsers(UserPageDTO dto) {
        // 1. 分页参数
        Page<User> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        // 2. 查询条件
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .like(StrUtil.isNotBlank(dto.getUsername()), "username", dto.getUsername())
                .eq(dto.getStatus() != null, "status", dto.getStatus());

        // 3. 排序规则
        wrapper.orderBy(true, dto.getAsc(),
                Boolean.TRUE.equals(dto.getSortedByUpdate()) ? "updated_at" : "created_at");

        // 4. 分页查询
        Page<User> result = userMapper.selectPage(page, wrapper);
        log.debug("【User Service】分页查询用户：{}", dto);

        return PageResult.of(result, UserPageVO.class);
    }

    @SuperAdminOnly
    @Override
    public void updateStatus(StatusDTO dto) {
        boolean success = update(new UpdateWrapper<User>()
                .eq("id", dto.getId())
                .set("status", dto.getStatus()));
        if (!success) throw new UserException(ResultInfo.USER_UPDATE_ERROR);
        log.debug("【User Service】更新用户状态：{}", dto);
    }

    @SuperAdminOnly
    @Override
    public void updatePassword(PasswordDTO dto) {
        boolean success = update(new UpdateWrapper<User>()
                .eq("id", dto.getId())
                .set("password", SecureUtil.md5(dto.getPassword())));
        if (!success) throw new UserException(ResultInfo.USER_UPDATE_ERROR);
        log.debug("【User Service】更新用户密码：{}", dto);
    }

    @SuperAdminOnly
    @Override
    public void removeUser(Long id) {
        boolean success = removeById(id);
        if (!success) {
            throw new ContactException(ResultInfo.USER_REMOVE_ERROR);
        }
        log.debug("【User Service】删除用户：{}", id);
    }
}