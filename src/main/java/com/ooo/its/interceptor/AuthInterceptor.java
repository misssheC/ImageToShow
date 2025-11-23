package com.ooo.its.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        // 如果用户已登录，放行
        if (session != null && session.getAttribute("qqNumber") != null) {
            return true;
        }

        String uri = request.getRequestURI();

        // 放行登录相关页面和所有静态资源
        if (uri.equals("/login") ||
                uri.equals("/logout") ||
                uri.equals("/bbback") ||           // 后台登录页面
                uri.equals("/adminlogin") ||        // 后台登录接口
                uri.startsWith("/css/") ||         // CSS 文件
                uri.startsWith("/js/") ||          // JavaScript 文件
                uri.startsWith("/images/") ||      // 图片文件
                uri.startsWith("/webjars/") ||     // WebJars 资源
                uri.startsWith("/static/") ||      // 静态资源
                uri.equals("/favicon.ico") ||
                uri.equals("/")) {
            return true;
        }
        response.sendRedirect("/login");
        return false;
    }
}
