package com.example.usermanagement.interceptor;

import com.example.usermanagement.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

/**
 * 登录拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS请求（跨域预检请求）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        // 放行登录和注册接口
        String uri = request.getRequestURI();
        if (uri.contains("/api/login") || uri.contains("/api/register")) {
            return true;
        }
        
        // 检查session中是否有登录用户
        HttpSession session = request.getSession();
        Object currentUser = session.getAttribute("currentUser");
        
        if (currentUser == null) {
            // 未登录，返回401错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            
            Result<Void> result = Result.error("用户未登录");
            PrintWriter writer = response.getWriter();
            writer.write(objectMapper.writeValueAsString(result));
            writer.flush();
            writer.close();
            
            return false;
        }
        
        return true;
    }
}
