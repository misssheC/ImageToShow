package com.ooo.its.repository;

import com.ooo.its.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RecordRep extends JpaRepository<Record,Long> {

    @Query("SELECT COALESCE(MAX(r.batch), 0) FROM Record r WHERE r.qqNumber = :qq")
    int findMaxBatchByQq(@Param("qq") String qq);

    Record findByQqNumberAndBatchAndIsPay(String qq , int batch , int state);

    List<Record> findAllByOrderByIdDesc();

}
