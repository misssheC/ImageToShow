package com.ooo.its.controller;

import com.ooo.its.dto.MonthlySummaryDTO;
import com.ooo.its.dto.YearlySummaryDTO;
import com.ooo.its.entity.Daily;
import com.ooo.its.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class AnalysisController {
    @Autowired
    AnalysisService analysisService;

    @GetMapping("/admin/daily")
    @ResponseBody
    public List<Daily> getDaily(@RequestParam(defaultValue = "1") int days) {
        return analysisService.ShowByDate(days);
    }
    @GetMapping("/admin/monthly")
    @ResponseBody
    public List<MonthlySummaryDTO> getMonthlySummary(
            @RequestParam(required = false) String year) {
        if (year != null && !year.trim().isEmpty()) {
            return analysisService.getMonthlySummaryByYear(year);
        }
        return analysisService.getMonthlySummary();
    }

    @GetMapping("/admin/yearly")
    @ResponseBody
    public List<YearlySummaryDTO> getYearlySummary() {
        return analysisService.getYearlySummary();
    }
    @GetMapping("/admin/todayLoginUsers")
    @ResponseBody
    public List<Map<String,String>> getTodayLoginUsers() {
        return analysisService.ShowTodayUserInfo();
    }
}
