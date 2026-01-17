package com.ooo.its.repository;

import com.ooo.its.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderRep extends JpaRepository<Order,Long> {

    List<Order> findAllByOrderByIdDesc(Pageable pageable);

    List<Order> findByQqNumberAndState(String qqNumber,int state);

    List<Order> findByQqNumberAndBatch(String qqNumber,int batch);

    List<Order> findAllByQqNumberOrderByIdDesc(String qq);
    @Modifying
    @Transactional
    void deleteByQqNumberAndGoodsIdAndState(String qqNumber, Long goodsId,int s);

    @Modifying
    @Transactional
    void deleteById(@NonNull Long id);
}
