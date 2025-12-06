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
            Model model,
            HttpSession session) {
        String region = getRegionFromIP(ipAddress);
        String device = deviceDetectionService.detectDeviceModel(deviceInfo);
        if (userService.validateUser(qqNumber, password,region)) {
            session.setAttribute("qqNumber", qqNumber);
            userInfoService.SaveLoginInfo(qqNumber,deviceInfo,ipAddress,device,region);
            logService.SaveLog(qqNumber,"成功登录 "+"IP:"+ipAddress+" 地区:"+region+" 设备:"+device,1);
            return "redirect:/folders";
        } else {
            logService.SaveLog(qqNumber,"登录失败 "+"IP:"+ipAddress+" 地区:"+region+" 设备:"+device,0);
            return "login";
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
            String url = "https://api.vore.top/api/IPdata?ip=" + ipAddress;

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());

                JsonNode ipdata = root.path("ipdata");
                String province = ipdata.path("info1").asText("");
                String city = ipdata.path("info2").asText("");
                String district = ipdata.path("info3").asText("");
                String isp = ipdata.path("isp").asText("");
                // 构建位置字符串：省-市-区
                StringBuilder location = new StringBuilder();
                if (!province.isEmpty()) {
                    location.append(province);
                }
                if (!city.isEmpty()) {
                    if (location.length() > 0) location.append("-");
                    location.append(city);
                }
                if (!district.isEmpty()) {
                    if (location.length() > 0) location.append("-");
                    location.append(district);
                }
                if (!isp.isEmpty()) {
                    if (location.length() > 0) location.append("-");
                    location.append(isp);
                }
                if (location.length() == 0) {
                    location.append(root.path("adcode").path("o").asText("未知"));
                }

                return location.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "未知";
    }
}