package com.example.usermanagement.controller;

import com.example.usermanagement.common.Result;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.scheduler.UserStateCheckScheduler;
import com.example.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录控制器
 */
@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params, HttpSession session) {
        String floginname = params.get("floginname");
        String fpassword = params.get("fpassword");

        Result<User> result = userService.login(floginname, fpassword);

        if (result.isSuccess()) {
            // 登录成功，将用户信息存入session
            User user = result.getData();
            session.setAttribute("currentUser", user);

            // 添加到用户状态监控列表
            UserStateCheckScheduler.addUser(user.getFid());

            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);

            return Result.success(result.getMessage(), data);
        } else {
            return Result.error(result.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        // 注册时设置类型为0（注册）
        user.setFtype(0);
        user.setFstate(1);
        return userService.addUser(user);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            // 从用户状态监控列表移除
            UserStateCheckScheduler.removeUser(user.getFid());
        }
        session.invalidate();
        return Result.success("登出成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/currentUser")
    public Result<User> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("用户未登录");
        }
        return Result.success(user);
    }

    /**
     * 检查用户状态
     */
    @GetMapping("/checkUserState")
    public Result<Void> checkUserState(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("用户未登录");
        }

        Result<Void> result = userService.checkUserState(user.getFid());
        if (!result.isSuccess()) {
            // 如果用户状态异常，清除session
            session.invalidate();
        }
        return result;
    }
}
