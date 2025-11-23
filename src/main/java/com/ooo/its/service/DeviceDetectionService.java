package com.ooo.its.service;


import com.ooo.its.entity.DeviceModel;
import com.ooo.its.repository.DeviceModelRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DeviceDetectionService {

    @Autowired
    private DeviceModelRep deviceModelRep;

    // 常见需要排除的关键词
    private static final String[] EXCLUDE_KEYWORDS = {
            "Linux", "Android", "Build", "AppleWebKit", "Version", "Mobile",
            "Safari", "Chrome", "Firefox", "Edge", "OPR", "Opera", "UBrowser",
            "QQBrowser", "Wechat", "MicroMessenger", "Windows", "iPhone",
            "iPad", "CPU", "API", "Device", "Model", "Browser", "WebKit",
            "KHTML", "like", "Gecko", "Mozilla", "WOW64", "x64"
    };

    public String detectDeviceModel(String userAgent) {
        // 从User-Agent中提取括号内的内容并分析
        List<String> possibleModels = extractPossibleModels(userAgent);

        // 在数据库中查找匹配的设备型号
        return findMatchingDeviceModel(possibleModels);
    }

    private List<String> extractPossibleModels(String userAgent) {
        // 提取括号内的所有内容
        Pattern bracketPattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher bracketMatcher = bracketPattern.matcher(userAgent);

        java.util.ArrayList<String> candidates = new java.util.ArrayList<>();

        while (bracketMatcher.find()) {
            String bracketContent = bracketMatcher.group(1);
            // 按分号分割括号内的内容
            String[] parts = bracketContent.split(";");

            for (String part : parts) {
                String trimmed = part.trim();

                // 处理设备型号后面跟着Build的情况
                trimmed = removeBuildSuffix(trimmed);

                // 过滤掉空字符串和明显不是型号的内容
                if (!trimmed.isEmpty() && !isExcludedContent(trimmed)) {
                    candidates.add(trimmed);
                }
            }
        }

        return candidates;
    }

    private String removeBuildSuffix(String content) {
        // 处理设备型号后面跟着Build的情况，例如："LIO-AN00 Build/RKQ1.200710.002"
        // 匹配模式：型号后面跟着 Build/ 或者 空格+Build
        Pattern buildPattern = Pattern.compile("(.+?)(?:\\s+Build/|\\s+Build\\s+|Build/).*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = buildPattern.matcher(content);

        if (matcher.matches()) {
            return matcher.group(1).trim();
        }

        // 处理其他常见的后缀模式
        // 1. 型号后面跟着 / 的情况，例如：SM-G975F/DS
        Pattern slashPattern = Pattern.compile("([A-Za-z0-9-]+)(?:/.*)?");
        matcher = slashPattern.matcher(content);
        if (matcher.matches()) {
            String baseModel = matcher.group(1);
            // 确保baseModel不是纯数字或过短
            if (!baseModel.matches("\\d+") && baseModel.length() >= 2) {
                return baseModel;
            }
        }

        return content;
    }

    private boolean isExcludedContent(String content) {
        // 检查是否包含需要排除的关键词
        for (String keyword : EXCLUDE_KEYWORDS) {
            if (content.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }

        // 排除纯数字或过短的内容
        if (content.matches("\\d+") || content.length() < 2) {
            return true;
        }

        // 排除明显是版本号的内容（包含点号）
        if (content.matches(".*\\d+\\.\\d+.*")) {
            return true;
        }

        // 排除看起来像Android版本的内容，例如：10、11、12等
        if (content.matches("\\d{1,2}") && Integer.parseInt(content) >= 5 && Integer.parseInt(content) <= 15) {
            return true;
        }

        return false;
    }

    private String findMatchingDeviceModel(List<String> possibleModels) {
        // 按顺序在数据库中查找，找到第一个匹配的
        for (String modelCandidate : possibleModels) {
            //System.out.println("尝试匹配型号: " + modelCandidate);

            // 先尝试精确匹配
            List<DeviceModel> exactMatches = deviceModelRep.findByModelCode(modelCandidate);
            if (!exactMatches.isEmpty()) {
                return exactMatches.get(0).getDeviceModel();
            }

            // 尝试包含匹配
            List<DeviceModel> containsMatches = deviceModelRep.findByModelCodeContaining(modelCandidate);
            if (!containsMatches.isEmpty()) {
                return containsMatches.get(0).getDeviceModel();
            }
        }

        // 如果所有候选都不匹配，尝试从原始User-Agent中提取可能的型号模式
        String fallbackModel = extractFallbackModel(possibleModels);
        if (fallbackModel != null) {
            List<DeviceModel> fallbackMatches = deviceModelRep.findByModelCodeContaining(fallbackModel);
            if (!fallbackMatches.isEmpty()) {
                return fallbackMatches.get(0).getDeviceModel();
            }
        }

        return "未知设备";
    }

    private String extractFallbackModel(List<String> possibleModels) {
        // 从候选列表中找出最可能是型号的字符串
        for (String candidate : possibleModels) {
            // 型号通常包含字母和数字，可能有横线
            if (candidate.matches("[A-Za-z0-9-]+")) {
                // 排除纯数字或纯字母
                if (!candidate.matches("\\d+") && !candidate.matches("[A-Za-z]+")) {
                    return candidate;
                }
            }
        }
        return null;
    }
}