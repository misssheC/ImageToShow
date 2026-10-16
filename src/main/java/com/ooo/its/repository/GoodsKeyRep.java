package com.ooo.its.repository;

import com.ooo.its.entity.GoodsKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoodsKeyRep extends JpaRepository<GoodsKey, Long> {
    List<GoodsKey> findByQqNumberAndBatch(String qqNumber, int batch);
}
