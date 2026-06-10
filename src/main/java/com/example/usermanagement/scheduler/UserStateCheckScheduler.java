package com.example.usermanagement.scheduler;

import com.example.usermanagement.common.Result;
import com.example.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户状态定时检查调度器
 */
@Component
public class UserStateCheckScheduler {
    
    @Autowired
    private UserService userService;
    
    // 存储用户状态异常的记录（用于前端轮询检测）
    private static final Map<Long, UserStateInfo> userStateMap = new ConcurrentHashMap<>();
    
    /**
     * 每5分钟执行一次用户状态检查
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void checkUserState() {
        // 检查所有在线用户的状态
        for (Long userId : userStateMap.keySet()) {
            UserStateInfo stateInfo = userStateMap.get(userId);
            if (stateInfo != null && stateInfo.isOnline()) {
                // 调用服务检查用户状态
                Result<Void> result = userService.checkUserState(userId);
                if (!result.isSuccess()) {
                    // 用户状态异常，标记为需要登出
                    stateInfo.setNeedLogout(true);
                    stateInfo.setLogoutMessage(result.getMessage());
                }
            }
        }
    }
    
    /**
     * 用户登录时调用，添加到监控列表
     */
    public static void addUser(Long userId) {
        UserStateInfo stateInfo = new UserStateInfo();
        stateInfo.setUserId(userId);
        stateInfo.setOnline(true);
        stateInfo.setNeedLogout(false);
        userStateMap.put(userId, stateInfo);
    }
    
    /**
     * 用户登出时调用，从监控列表移除
     */
    public static void removeUser(Long userId) {
        userStateMap.remove(userId);
    }
    
    /**
     * 检查用户是否需要登出
     */
    public static UserStateInfo checkNeedLogout(Long userId) {
        return userStateMap.get(userId);
    }
    
    /**
     * 清除用户的登出标记
     */
    public static void clearLogoutFlag(Long userId) {
        UserStateInfo stateInfo = userStateMap.get(userId);
        if (stateInfo != null) {
            stateInfo.setNeedLogout(false);
            stateInfo.setLogoutMessage(null);
        }
    }
    
    /**
     * 用户状态信息类
     */
    public static class UserStateInfo {
        private Long userId;
        private boolean online;
        private boolean needLogout;
        private String logoutMessage;
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public boolean isOnline() {
            return online;
        }
        
        public void setOnline(boolean online) {
            this.online = online;
        }
        
        public boolean isNeedLogout() {
            return needLogout;
        }
        
        public void setNeedLogout(boolean needLogout) {
            this.needLogout = needLogout;
        }
        
        public String getLogoutMessage() {
            return logoutMessage;
        }
        
        public void setLogoutMessage(String logoutMessage) {
            this.logoutMessage = logoutMessage;
        }
    }
}
