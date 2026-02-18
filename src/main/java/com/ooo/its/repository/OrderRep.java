package com.ooo.its.repository;

import com.ooo.its.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderRep extends JpaRepository<Order,Long> {

    @Query("SELECT COUNT(o) FROM Order o WHERE o.qqNumber = :qq AND o.batch = :batch")
    int countByQqAndBatch(@Param("qq") String qq, @Param("batch") int batch);
    List<Order> findAllByOrderByIdDesc(Pageable pageable);

    List<Order> findByQqNumberAndState(String qqNumber,int state);

    List<Order> findByQqNumberAndStateAndBatch(String qq , int state,int batch);

    List<Order>findByQqNumberAndStateOrderByIdDesc(String qqNumber, int state, Pageable pageable);

    List<Order> findByQqNumberAndBatch(String qqNumber,int batch);

    List<Order> findAllByQqNumberOrderByIdDesc(String qq,Pageable pageable);

    List<Order> findAllByStateOrderByIdDesc(int state,Pageable pageable);
    @Modifying
    @Transactional
    void deleteByQqNumberAndGoodsIdAndState(String qqNumber, Long goodsId,int s);

    @Modifying
    @Transactional
    void deleteById(@NonNull Long id);
}
