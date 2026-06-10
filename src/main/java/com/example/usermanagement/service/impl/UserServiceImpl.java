package com.example.usermanagement.service.impl;

import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.common.Result;
import com.example.usermanagement.dto.UserEditDTO;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.mapper.UserMapper;
import com.example.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    // 手机号正则表达式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    // 邮箱正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    // 密码正则表达式（英文+数字）
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,50}$");
    
    @Override
    public Result<User> login(String floginname, String fpassword) {
        // 参数校验
        if (!StringUtils.hasText(floginname)) {
            return Result.error("用户名不能为空");
        }
        if (!StringUtils.hasText(fpassword)) {
            return Result.error("密码不能为空");
        }
        
        // 查询用户
        User user = userMapper.selectByLoginName(floginname);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 校验用户状态
        if (user.getFstate() == 0) {
            return Result.error("账号已被禁用");
        }
        if (user.getFstate() == 2) {
            return Result.error("账号已被冻结");
        }
        
        // 校验密码（明文比较）
        if (!fpassword.equals(user.getFpassword())) {
            return Result.error("账号或密码错误，请重试");
        }
        
        // 清除密码后返回
        user.setFpassword(null);
        user.generateStateName();
        user.generateTypeName();
        return Result.success("登录成功", user);
    }
    
    @Override
    @Transactional
    public Result<Void> addUser(User user) {
        // 参数校验
        Result<Void> validateResult = validateUser(user, true);
        if (!validateResult.isSuccess()) {
            return validateResult;
        }
        
        // 检查用户名是否已存在
        User existUser = userMapper.selectByLoginName(user.getFloginname());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }
        
        // 设置默认值
        if (user.getFstate() == null) {
            user.setFstate(1);
        }
        if (user.getFtype() == null) {
            user.setFtype(0);
        }
        if (user.getFcreatedatetime() == null) {
            user.setFcreatedatetime(LocalDateTime.now());
        }
        
        // 保存用户
        int result = userMapper.insert(user);
        if (result > 0) {
            return Result.success("添加成功");
        } else {
            return Result.error("添加失败");
        }
    }
    
    @Override
    @Transactional
    public Result<Void> deleteUser(Long fid) {
        if (fid == null) {
            return Result.error("用户ID不能为空");
        }
        
        // 查询用户
        User user = userMapper.selectById(fid);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 禁止删除admin用户
        if ("admin".equals(user.getFloginname())) {
            return Result.error("系统管理员不允许删除");
        }
        
        int result = userMapper.deleteById(fid);
        if (result > 0) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @Override
    @Transactional
    public Result<Void> updateUser(User user) {
        if (user.getFid() == null) {
            return Result.error("用户ID不能为空");
        }
        
        // 查询用户
        User existUser = userMapper.selectById(user.getFid());
        if (existUser == null) {
            return Result.error("用户不存在");
        }
        
        // 禁止修改admin用户的用户名
        if ("admin".equals(existUser.getFloginname())) {
            // admin用户只能修改自己的信息，不能修改状态为禁用或冻结
            if (user.getFstate() != null && user.getFstate() != 1) {
                return Result.error("系统管理员不允许禁用或冻结");
            }
        }
        
        // 参数校验
        Result<Void> validateResult = validateUser(user, false);
        if (!validateResult.isSuccess()) {
            return validateResult;
        }
        
        // 密码明文存储
        
        int result = userMapper.update(user);
        if (result > 0) {
            return Result.success("修改成功");
        } else {
            return Result.error("修改失败");
        }
    }
    
    @Override
    public Result<List<User>> queryUsers(Long fid, Long unfid, String floginname, String fnickname,
                                          Integer fstate, LocalDate fbirthdaybegin, LocalDate fbirthdayend,
                                          String fphonenumber, String femail, Integer ftype,
                                          LocalDateTime fcreatedatetimebegin, LocalDateTime fcreatedatetimeend,
                                          Long fcreateuserid, String fcondition) {
        List<User> list = userMapper.selectList(fid, unfid, floginname, fnickname, fstate,
                fbirthdaybegin, fbirthdayend, fphonenumber, femail, ftype,
                fcreatedatetimebegin, fcreatedatetimeend, fcreateuserid, fcondition);
        
        // 设置状态名称和类型名称
        for (User user : list) {
            user.setFpassword(null);
            user.generateStateName();
            user.generateTypeName();
        }
        
        return Result.success(list);
    }
    
    @Override
    public Result<PageResult<User>> queryUsersByPage(Long fid, Long unfid, String floginname, String fnickname,
                                                      Integer fstate, LocalDate fbirthdaybegin, LocalDate fbirthdayend,
                                                      String fphonenumber, String femail, Integer ftype,
                                                      LocalDateTime fcreatedatetimebegin, LocalDateTime fcreatedatetimeend,
                                                      Long fcreateuserid, String fcondition,
                                                      Integer pagenum, Integer pagesize) {
        // 参数校验
        if (pagenum == null || pagenum < 1) {
            pagenum = 1;
        }
        if (pagesize == null || pagesize < 1) {
            pagesize = 10;
        }
        
        // 计算偏移量
        int offset = (pagenum - 1) * pagesize;
        
        // 查询总数
        int total = userMapper.selectCount(fid, unfid, floginname, fnickname, fstate,
                fbirthdaybegin, fbirthdayend, fphonenumber, femail, ftype,
                fcreatedatetimebegin, fcreatedatetimeend, fcreateuserid, fcondition);
        
        // 查询列表
        List<User> list = userMapper.selectListByPage(fid, unfid, floginname, fnickname, fstate,
                fbirthdaybegin, fbirthdayend, fphonenumber, femail, ftype,
                fcreatedatetimebegin, fcreatedatetimeend, fcreateuserid, fcondition,
                offset, pagesize);
        
        // 设置状态名称和类型名称
        for (User user : list) {
            user.setFpassword(null);
            user.generateStateName();
            user.generateTypeName();
        }
        
        PageResult<User> pageResult = new PageResult<>(pagenum, pagesize, (long) total, list);
        return Result.success(pageResult);
    }
    
    @Override
    public Result<User> getUserById(Long fid) {
        if (fid == null) {
            return Result.error("用户ID不能为空");
        }
        
        User user = userMapper.selectById(fid);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        user.setFpassword(null);
        user.generateStateName();
        user.generateTypeName();
        return Result.success(user);
    }

    @Override
    public Result<UserEditDTO> getUserDetail(Long fid) {
        if (fid == null) {
            return Result.error("用户ID不能为空");
        }

        User user = userMapper.selectById(fid);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 生成状态和类型名称
        user.generateStateName();
        user.generateTypeName();

        // 转换为 DTO（包含密码）
        UserEditDTO dto = new UserEditDTO(user);
        return Result.success(dto);
    }

    @Override
    public Result<List<User>> getAllCreators() {
        List<User> list = userMapper.selectAllCreators();
        return Result.success(list);
    }
    
    @Override
    public Result<Void> checkUserState(Long fid) {
        if (fid == null) {
            return Result.error("用户ID不能为空");
        }
        
        User user = userMapper.selectById(fid);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if (user.getFstate() == 0) {
            return Result.error("账号已被禁用");
        }
        if (user.getFstate() == 2) {
            return Result.error("账号已被冻结");
        }
        
        return Result.success();
    }
    
    /**
     * 校验用户参数
     */
    private Result<Void> validateUser(User user, boolean isAdd) {
        // 新增时校验用户名
        if (isAdd) {
            if (!StringUtils.hasText(user.getFloginname())) {
                return Result.error("用户名不能为空");
            }
            if (user.getFloginname().length() > 50) {
                return Result.error("用户名长度不能超过50个字符");
            }
        }
        
        // 校验密码
        if (isAdd || StringUtils.hasText(user.getFpassword())) {
            if (!StringUtils.hasText(user.getFpassword())) {
                return Result.error("密码不能为空");
            }
            if (user.getFpassword().length() > 50) {
                return Result.error("密码长度不能超过50个字符");
            }
            if (!PASSWORD_PATTERN.matcher(user.getFpassword()).matches()) {
                return Result.error("密码必须包含英文和数字，且长度至少6位");
            }
        }
        
        // 校验昵称
        if (!StringUtils.hasText(user.getFnickname())) {
            return Result.error("昵称不能为空");
        }
        if (user.getFnickname().length() > 50) {
            return Result.error("昵称长度不能超过50个字符");
        }
        
        // 校验状态
        if (user.getFstate() != null) {
            if (user.getFstate() < 0 || user.getFstate() > 2) {
                return Result.error("状态值无效");
            }
        }
        
        // 校验手机号
        if (StringUtils.hasText(user.getFphonenumber())) {
            if (!PHONE_PATTERN.matcher(user.getFphonenumber()).matches()) {
                return Result.error("手机号格式不正确");
            }
        }
        
        // 校验邮箱
        if (StringUtils.hasText(user.getFemail())) {
            if (user.getFemail().length() > 50) {
                return Result.error("邮箱长度不能超过50个字符");
            }
            if (!EMAIL_PATTERN.matcher(user.getFemail()).matches()) {
                return Result.error("邮箱格式不正确");
            }
        }
        
        return Result.success();
    }
}
