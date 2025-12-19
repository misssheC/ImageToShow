package com.ooo.its.repository;

import com.ooo.its.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderRep extends JpaRepository<Order,Long> {

    List<Order> findAllByOrderByIdDesc(Pageable pageable);

    List<Order> findByQqNumberAndState(String qqNumber,int state);

    @Modifying
    @Transactional
    void deleteByQqNumberAndGoodsIdAndState(String qqNumber, Long goodsId,int s);
}
