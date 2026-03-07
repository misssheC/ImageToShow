package com.ooo.its.service;

import com.ooo.its.dto.MonthlySummaryDTO;
import com.ooo.its.dto.YearlySummaryDTO;
import com.ooo.its.entity.Daily;
import com.ooo.its.entity.Log;
import com.ooo.its.entity.UserInfo;
import com.ooo.its.repository.DailyRep;
import com.ooo.its.repository.LogRep;
import com.ooo.its.repository.UserInfoRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class AnalysisService {
    @Autowired
    private LogRep logRep;
    @Autowired
    private UserInfoRep userInfoRep;
    @Autowired
    private DailyRep dailyRep;
    private String getTodayDateString() {
        return LocalDate.now().toString();
    }

    private Date getStartOfToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }
    private Date getEndOfToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();
        return Date.from(startOfNextDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    public void LoginStatistics(String qq) {
        Date start = getStartOfToday();
        Date end = getEndOfToday();
        List<Log> logs = logRep.findByUserAndTypeAndTimeBetween(qq, 1,start, end);
        if (logs.isEmpty()) {
            Daily daily = FindOrCreateNewDaily(getTodayDateString());
            daily.setLogin(daily.getLogin() + 1);
            dailyRep.save(daily);
        }
    }

    public void AddNewStatistics() {
        Daily daily = FindOrCreateNewDaily(getTodayDateString());
        daily.setCart(daily.getCart() + 1);
        dailyRep.save(daily);
    }

    public void ViewStatistics() {
        Daily daily = FindOrCreateNewDaily(getTodayDateString());
        daily.setView(daily.getView() + 1);
        dailyRep.save(daily);
    }

    public void LoginErrorStatistics(int type) {
        Daily daily = FindOrCreateNewDaily(getTodayDateString());
        if (type == 0) {
            daily.setLerror(daily.getLerror() + 1);
        } else {
            daily.setRerror(daily.getRerror() + 1);
        }
        dailyRep.save(daily);
    }

    public void IncomeStatistics(int amount) {
        Daily daily = FindOrCreateNewDaily(getTodayDateString());
        daily.setIncome(daily.getIncome() + amount);
        dailyRep.save(daily);
    }

    public List<Map<String, String>> ShowTodayUserInfo() {
        List<String> users = FindLoginUser();
        List<Map<String, String>> result = new ArrayList<>();
        for (String user : users) {
            Map<String, String> t = new HashMap<>();
            t.put("qq", user);
            UserInfo info = userInfoRep.findByQqNumber(user).orElse(null);
            t.put("money", info != null ? String.valueOf(info.getLumpSum()) : "0");
            t.put("region", info != null ? info.getRegion() : "");
            t.put("device", info != null ? info.getDevice() : "");
            result.add(t);
        }
        return result;
    }

    public List<String> FindLoginUser() {
        Date start = getStartOfToday();
        Date end = getEndOfToday();
        List<Log> logs = logRep.findByTypeAndTimeBetween(1, start, end);
        return logs.stream()
                .map(Log::getUser)
                .distinct()
                .toList();
    }

    public List<Daily> ShowByDate(int days) {
        return dailyRep.findRecentDays(days);
    }

    public List<MonthlySummaryDTO> getMonthlySummary() {
        List<Object[]> results = dailyRep.getMonthlySummary();
        return convertToMonthlyDTOList(results);
    }
    public List<MonthlySummaryDTO> getMonthlySummaryByYear(String year) {
        List<Object[]> results = dailyRep.getMonthlySummaryByYear(year);
        return convertToMonthlyDTOList(results);
    }

    public List<YearlySummaryDTO> getYearlySummary() {
        List<Object[]> results = dailyRep.getYearlySummary();
        List<YearlySummaryDTO> list = new ArrayList<>();
        for (Object[] row : results) {
            YearlySummaryDTO dto = new YearlySummaryDTO(
                    (String) row[0],                     // year
                    ((Number) row[1]).intValue(),        // totalLogin
                    ((Number) row[2]).intValue(),        // totalView
                    ((Number) row[3]).intValue(),        // totalCart
                    ((Number) row[4]).intValue(),        // totalIncome
                    ((Number) row[5]).intValue(),        // totalLerror
                    ((Number) row[6]).intValue()         // totalRerror
            );
            list.add(dto);
        }
        return list;
    }
    private List<MonthlySummaryDTO> convertToMonthlyDTOList(List<Object[]> results) {
        List<MonthlySummaryDTO> list = new ArrayList<>();
        for (Object[] row : results) {
            MonthlySummaryDTO dto = new MonthlySummaryDTO(
                    (String) row[0],                     // yearMonth
                    ((Number) row[1]).intValue(),        // totalLogin
                    ((Number) row[2]).intValue(),        // totalView
                    ((Number) row[3]).intValue(),        // totalCart
                    ((Number) row[4]).intValue(),        // totalIncome
                    ((Number) row[5]).intValue(),        // totalLerror
                    ((Number) row[6]).intValue()         // totalRerror
            );
            list.add(dto);
        }
        return list;
    }

    public Daily FindOrCreateNewDaily(String date) {
        Daily existing = dailyRep.findByDate(date);
        if (existing == null) {
            Daily d = new Daily();
            d.setDate(date);
            d.setLogin(0);
            d.setView(0);
            d.setIncome(0);
            d.setLerror(0);
            d.setRerror(0);
            dailyRep.save(d);
            return d;
        }
        return existing;
    }
}