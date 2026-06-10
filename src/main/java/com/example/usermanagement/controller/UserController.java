package com.example.usermanagement.controller;

import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.common.Result;
import com.example.usermanagement.dto.UserEditDTO;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 新增用户
     */
    @PostMapping("/add")
    public Result<Void> addUser(@RequestBody User user, HttpSession session) {
        // 获取当前登录用户
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        // 设置创建人
        user.setFcreateuserid(currentUser.getFid());
        // 设置类型为1（添加）
        user.setFtype(1);
        
        return userService.addUser(user);
    }
    
    /**
     * 删除用户
     */
    @PostMapping("/delete")
    public Result<Void> deleteUser(@RequestBody User user, HttpSession session) {
        // 获取当前登录用户
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        return userService.deleteUser(user.getFid());
    }
    
    /**
     * 修改用户
     */
    @PostMapping("/edit")
    public Result<Void> updateUser(@RequestBody User user, HttpSession session) {
        // 获取当前登录用户
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        return userService.updateUser(user);
    }
    
    /**
     * 查询用户列表（不分页）
     */
    @GetMapping("/query")
    public Result<List<User>> queryUsers(
            @RequestParam(required = false) Long fid,
            @RequestParam(required = false) Long unfid,
            @RequestParam(required = false) String floginname,
            @RequestParam(required = false) String fnickname,
            @RequestParam(required = false) Integer fstate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fbirthdaybegin,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fbirthdayend,
            @RequestParam(required = false) String fphonenumber,
            @RequestParam(required = false) String femail,
            @RequestParam(required = false) Integer ftype,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime fcreatedatetimebegin,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime fcreatedatetimeend,
            @RequestParam(required = false) Long fcreateuserid,
            @RequestParam(required = false) String fcondition,
            HttpSession session) {
        
        // 获取当前登录用户
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        return userService.queryUsers(fid, unfid, floginname, fnickname, fstate,
                fbirthdaybegin, fbirthdayend, fphonenumber, femail, ftype,
                fcreatedatetimebegin, fcreatedatetimeend, fcreateuserid, fcondition);
    }
    
    /**
     * 查询用户列表（分页）
     */
    @GetMapping("/query/page")
    public Result<PageResult<User>> queryUsersByPage(
            @RequestParam(required = false) Long fid,
            @RequestParam(required = false) Long unfid,
            @RequestParam(required = false) String floginname,
            @RequestParam(required = false) String fnickname,
            @RequestParam(required = false) Integer fstate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fbirthdaybegin,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fbirthdayend,
            @RequestParam(required = false) String fphonenumber,
            @RequestParam(required = false) String femail,
            @RequestParam(required = false) Integer ftype,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime fcreatedatetimebegin,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime fcreatedatetimeend,
            @RequestParam(required = false) Long fcreateuserid,
            @RequestParam(required = false) String fcondition,
            @RequestParam(defaultValue = "1") Integer pagenum,
            @RequestParam(defaultValue = "10") Integer pagesize,
            HttpSession session) {
        
        // 获取当前登录用户
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        return userService.queryUsersByPage(fid, unfid, floginname, fnickname, fstate,
                fbirthdaybegin, fbirthdayend, fphonenumber, femail, ftype,
                fcreatedatetimebegin, fcreatedatetimeend, fcreateuserid, fcondition,
                pagenum, pagesize);
    }
    
    /**
     * 根据ID查询用户
     */
    @GetMapping("/get/{id}")
    public Result<User> getUserById(@PathVariable("id") Long id, HttpSession session) {
        // 获取当前登录用户
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("用户未登录");
        }

        return userService.getUserById(id);
    }

    /**
     * 根据ID查询用户详情（包含密码，用于编辑）
     */
    @GetMapping("/detail/{id}")
    public Result<UserEditDTO> getUserDetail(@PathVariable("id") Long id, HttpSession session) {
        // 获取当前登录用户
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("用户未登录");
        }

        return userService.getUserDetail(id);
    }
    
    /**
     * 查询所有创建人
     */
    @GetMapping("/creators")
    public Result<List<User>> getAllCreators(HttpSession session) {
        // 获取当前登录用户
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        return userService.getAllCreators();
    }
}
