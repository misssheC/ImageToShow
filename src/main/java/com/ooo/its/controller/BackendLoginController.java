package com.ooo.its.controller;

import com.ooo.its.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Controller
public class BackendLoginController {
    @Autowired
    private AdminAuthService adminAuthService;

    @PostMapping("/adminlogin")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        boolean isAuthenticated = adminAuthService.Authenticate(username, password);

        if (isAuthenticated) {
            HttpSession session = request.getSession();
            session.setAttribute("adminUsername", username);

            response.put("success", true);
            response.put("message", "登录成功");
            response.put("redirectUrl", "/dashboard");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.status(401).body(response);
        }
    }
    @GetMapping("/bbback")
    public String Backend(){
        return "backend";
    }
    @GetMapping("/dashboard")
    public String Dashboard(){
        return "dashboard";
    }
    public static class LoginRequest {
        private String username;
        private String password;

        // getters and setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}