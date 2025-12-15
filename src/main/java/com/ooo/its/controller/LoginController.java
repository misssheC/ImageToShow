package com.ooo.its.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ooo.its.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private LogService logService;
    @Autowired
    private DeviceDetectionService deviceDetectionService;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String qqNumber,
            @RequestParam String password,
            @RequestParam String deviceInfo,
            @RequestParam String ipAddress,
            HttpSession session) {
        String region = getRegionFromIP(ipAddress);
        boolean accountValid = userService.validateUser(qqNumber, password, region);

        boolean regionValid = isRegionValid(region);

        if (accountValid) {

            int identity = userService.Identity(qqNumber);
            if (identity == 1) {
                logService.SaveLog(qqNumber, "登录失败：黑名单用户 " + "IP:" + ipAddress + " 地区:" + region, 0);
                return "redirect:/login?error=" + URLEncoder.encode("账号已被禁止登录", StandardCharsets.UTF_8);
            }
            boolean skipVpnCheck = (identity == 2);
            boolean regionCheckPassed = regionValid || skipVpnCheck;
            if (regionCheckPassed) {
                session.setAttribute("qqNumber", qqNumber);
                String device = deviceDetectionService.detectDeviceModel(deviceInfo);
                userInfoService.SaveLoginInfo(qqNumber, deviceInfo, ipAddress, device, region);
                String logMsg = "成功登录";
                if (identity == 2) {
                    logMsg += "（VIP用户跳过VPN检测）";
                }
                logMsg += " IP:" + ipAddress + " 地区:" + region;
                logService.SaveLog(qqNumber, logMsg, 1);
                return "redirect:/folders";
            } else {
                logService.SaveLog(qqNumber, "登录失败：地区不符合要求 " + "IP:" + ipAddress + " 地区:" + region, 0);
                return "redirect:/login?error=" + URLEncoder.encode("请关闭VPN", StandardCharsets.UTF_8);
            }

        }
        else {
            logService.SaveLog(qqNumber, "登录失败：账号或密码错误 " + "IP:" + ipAddress + " 地区:" + region, 0);
            return "redirect:/login?error=" + URLEncoder.encode("账号不存在或密码错误或登录地点发生变更", StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    @GetMapping("/folders")
    public String showFolders() {
        return "folders";
    }

    @GetMapping("/")
    public String ShowLoginPage(Model model) {
        return "login";
    }

    private String getRegionFromIP(String ipAddress) {
        try {
            String url = "http://ip-api.com/json/" + ipAddress + "?lang=zh-CN&fields=status,message,country,regionName,city,isp";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                if (!"success".equals(root.path("status").asText()))
                    return "未知";
                String country = root.path("country").asText("");
                String region = root.path("regionName").asText("");
                String city = root.path("city").asText("");
                String isp = root.path("isp").asText("");
                StringBuilder location = new StringBuilder();
                if (!country.isEmpty()) {
                    location.append(country);
                }
                if (!region.isEmpty()) {
                    if (!location.isEmpty()) location.append("-");
                    location.append(region);
                }
                if (!city.isEmpty()) {
                    if (!location.isEmpty()) location.append("-");
                    location.append(city);
                }
                if (!isp.isEmpty()) {
                    if (!location.isEmpty()) location.append("-");
                    location.append(isp);
                }

                return location.isEmpty() ? "未知" : location.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "未知";
    }
    private boolean isRegionValid(String region) {
        return region.contains("中国");
    }
}