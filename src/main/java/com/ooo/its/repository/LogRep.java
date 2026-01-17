package com.ooo.its.repository;

import com.ooo.its.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface LogRep extends JpaRepository<Log,Long> {

    @Query("SELECT l FROM Log l WHERE l.time >= :startTime ORDER BY l.time DESC")
    List<Log> findLast24HoursLogs(@Param("startTime") Date startTime);

    List<Log> findByUserOrderByTimeDesc(String qq);

    List<Log> findByType(int type);


}
