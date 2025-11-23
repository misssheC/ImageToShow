package com.ooo.its.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {
    private static final String[] ADMIN_WHITE_LIST = {
            "/adminlogin",
            "/bbback",
            "/css/",
            "/js/",
            "/img/"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = requestURI.substring(contextPath.length());

        if (isAdminWhiteList(path)) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("adminUsername") == null) {

            response.sendRedirect(contextPath + "/bbback");
            return false;
        }

        return true;
    }

    private boolean isAdminWhiteList(String path) {
        for (String whitePath : ADMIN_WHITE_LIST) {
            if (path.startsWith(whitePath)) {
                return true;
            }
        }
        return false;
    }
}