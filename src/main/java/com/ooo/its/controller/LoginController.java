package com.ooo.its.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ooo.its.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
    @Autowired
    private AnalysisService analysisService;
    @Autowired
    private QqService qqService;
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

        String device = deviceDetectionService.detectDeviceModel(deviceInfo);
        boolean accountValid = userService.validateUser(qqNumber, password, device);
        if (accountValid) {
            int identity = userService.Identity(qqNumber);
            if (identity == 1) {
                logService.SaveLog(qqNumber, "登录失败：黑名单用户 " + "IP:" + ipAddress + " 地区:" + region, 0);
                qqService.sendLoginMessage(qqNumber, false, region, device, "账号已被禁止登录（黑名单）");
                return "redirect:/login?error=" + URLEncoder.encode("账号已被禁止登录", StandardCharsets.UTF_8);
            }

            boolean skipVpnCheck = (identity == 2 || identity == 3);
            boolean regionCheckPassed = region.contains("中国") || skipVpnCheck;
            boolean regionMatch = (region.equals(userService.GetLastRegion(qqNumber)));

            if (regionCheckPassed) {
                session.setAttribute("qqNumber", qqNumber);
                StringBuilder logMsg = new StringBuilder("成功登录");
                String extraInfo = "";
                if (identity == 2) {
                    logMsg.append("（VIP用户跳过VPN检测）");
                    extraInfo = "VIP用户";
                } else if (identity == 3) {
                    logMsg.append("（海外用户跳过VPN检测）");
                    extraInfo = "海外用户";
                } else {
                    extraInfo = "普通用户";
                }

                if (regionMatch) {
                    logMsg.append("登录异常，登录地点发生变更！");
                    extraInfo += "（登录地点发生变更）";
                }
                logMsg.append(" IP:").append(ipAddress)
                        .append(" 地区:").append(region)
                        .append(" 登录设备：").append(device);
                analysisService.LoginStatistics(qqNumber);
                logService.SaveLog(qqNumber, logMsg.toString(), 1);
                qqService.sendLoginMessage(qqNumber, true, region, device, extraInfo);
                userInfoService.SaveLoginInfo(qqNumber, deviceInfo, ipAddress, device, region); 
                return "redirect:/folders";
            } else {
                logService.SaveLog(qqNumber, "登录失败：地区不符合要求 " + " 地区:" + region + " 登录设备："+ device, 0);
                analysisService.LoginErrorStatistics(1);
                qqService.sendLoginMessage(qqNumber, false, region, device, "地区不符合要求");
                return "redirect:/login?error=" + URLEncoder.encode("请关闭VPN 退出浏览器等待几秒重新进入", StandardCharsets.UTF_8);
            }
        } else {
            logService.SaveLog(qqNumber, "登录失败：账号或密码错误 " + " 地区:" + region + " 登录设备："+ device, 0);
            analysisService.LoginErrorStatistics(0);
            qqService.sendLoginMessage(qqNumber, false, region, device, "账号或密码错误");
            return "redirect:/login?error=" + URLEncoder.encode("账号不存在或密码错误或登录设备发生变更,请返回到原设备登录", StandardCharsets.UTF_8);
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
        if (ipAddress != null && !ipAddress.trim().isEmpty()) {
            try {
                String url = "https://ip9.com.cn/get?ip=" + ipAddress;
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
                headers.set("Referer", "https://ip9.com.cn/");
                headers.set("Accept", "application/json, text/javascript, */*; q=0.01");
                HttpEntity<String> entity = new HttpEntity(headers);
                restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, new Object[0]);
                String responseBody = (String)response.getBody();
                if (response.getStatusCode() == HttpStatus.OK && responseBody != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(responseBody);
                    if (root.path("ret").asInt() != 200) {
                        return "未知";
                    }

                    JsonNode dataNode = root.path("data");
                    String country = dataNode.path("country").asText("");
                    String region = dataNode.path("prov").asText("");
                    String city = dataNode.path("city").asText("");
                    String area = dataNode.path("area").asText("");
                    String isp = dataNode.path("isp").asText("");
                    StringBuilder location = new StringBuilder();
                    if (!country.isEmpty()) {
                        location.append(country);
                    }

                    if (!region.isEmpty()) {
                        if (!location.isEmpty()) {
                            location.append("-");
                        }

                        location.append(region);
                    }

                    if (!city.isEmpty()) {
                        if (!location.isEmpty()) {
                            location.append("-");
                        }

                        location.append(city);
                    }

                    if (!area.isEmpty()) {
                        if (!location.isEmpty()) {
                            location.append("-");
                        }

                        location.append(area);
                    }

                    if (!isp.isEmpty()) {
                        if (!location.isEmpty()) {
                            location.append(" (");
                        }

                        location.append(isp);
                        if (!location.isEmpty()) {
                            location.append(")");
                        }
                    }

                    return location.isEmpty() ? "未知" : location.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "未知";
        } else {
            return "未知";
        }
    }
}