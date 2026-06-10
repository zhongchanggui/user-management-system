package com.example.usermanagement.service;

import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.common.Result;
import com.example.usermanagement.dto.UserEditDTO;
import com.example.usermanagement.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     */
    Result<User> login(String floginname, String fpassword);

    /**
     * 新增用户
     */
    Result<Void> addUser(User user);

    /**
     * 删除用户
     */
    Result<Void> deleteUser(Long fid);

    /**
     * 修改用户
     */
    Result<Void> updateUser(User user);

    /**
     * 查询用户列表（不分页）
     */
    Result<List<User>> queryUsers(Long fid, Long unfid, String floginname, String fnickname,
                                   Integer fstate, LocalDate fbirthdaybegin, LocalDate fbirthdayend,
                                   String fphonenumber, String femail, Integer ftype,
                                   LocalDateTime fcreatedatetimebegin, LocalDateTime fcreatedatetimeend,
                                   Long fcreateuserid, String fcondition);

    /**
     * 查询用户列表（分页）
     */
    Result<PageResult<User>> queryUsersByPage(Long fid, Long unfid, String floginname, String fnickname,
                                               Integer fstate, LocalDate fbirthdaybegin, LocalDate fbirthdayend,
                                               String fphonenumber, String femail, Integer ftype,
                                               LocalDateTime fcreatedatetimebegin, LocalDateTime fcreatedatetimeend,
                                               Long fcreateuserid, String fcondition,
                                               Integer pagenum, Integer pagesize);

    /**
     * 根据ID查询用户
     */
    Result<User> getUserById(Long fid);

    /**
     * 根据ID查询用户详情（包含密码，用于编辑）
     */
    Result<UserEditDTO> getUserDetail(Long fid);

    /**
     * 查询所有创建人
     */
    Result<List<User>> getAllCreators();

    /**
     * 校验用户状态
     */
    Result<Void> checkUserState(Long fid);
}
