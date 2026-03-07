package com.ooo.its.repository;

import com.ooo.its.entity.Daily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DailyRep extends JpaRepository<Daily,Long> {
    Daily findByDate(String date);
    @Query(value = "SELECT * FROM daily ORDER BY day_id DESC LIMIT :limit", nativeQuery = true)
    List<Daily> findRecentDays(@Param("limit") int limit);

    @Query("SELECT SUBSTRING(d.date, 1, 7) as yearMonth, " +
            "SUM(d.login), SUM(d.view), SUM(d.cart), SUM(d.income), " +
            "SUM(d.lerror), SUM(d.rerror) " +
            "FROM Daily d GROUP BY 1 ORDER BY 1 DESC")
    List<Object[]> getMonthlySummary();

    @Query("SELECT SUBSTRING(d.date, 1, 7) as yearMonth, " +
            "SUM(d.login), SUM(d.view), SUM(d.cart), SUM(d.income), " +
            "SUM(d.lerror), SUM(d.rerror) " +
            "FROM Daily d WHERE SUBSTRING(d.date, 1, 4) = :year " +
            "GROUP BY 1 ORDER BY 1 DESC")
    List<Object[]> getMonthlySummaryByYear(@Param("year") String year);

    @Query("SELECT SUBSTRING(d.date, 1, 4) as year, " +
            "SUM(d.login), SUM(d.view), SUM(d.cart), SUM(d.income), " +
            "SUM(d.lerror), SUM(d.rerror) " +
            "FROM Daily d GROUP BY 1 ORDER BY 1 DESC")
    List<Object[]> getYearlySummary();

}
