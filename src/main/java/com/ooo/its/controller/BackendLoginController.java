package com.ooo.its.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Controller
public class BackendLoginController {
    private static final String FIXED_USERNAME = "me";
    private static final String FIXED_PASSWORD = "okok";

    @PostMapping("/adminlogin")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        if (FIXED_USERNAME.equals(loginRequest.getUsername()) &&
                FIXED_PASSWORD.equals(loginRequest.getPassword())) {
            HttpSession session = request.getSession();
            session.setAttribute("adminUsername", loginRequest.getUsername());

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