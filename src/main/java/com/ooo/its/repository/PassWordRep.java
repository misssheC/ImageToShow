package com.ooo.its.repository;

import com.ooo.its.entity.Passwd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassWordRep extends JpaRepository<Passwd, Long>{
    Optional<Passwd> findByQqAndBatch(String qq,int batch);

    Optional<Passwd> findTopByQqOrderByBatchDesc(String qq);
}
