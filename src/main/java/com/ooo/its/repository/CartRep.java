package com.ooo.its.repository;

import com.ooo.its.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartRep extends JpaRepository<Cart,Long> {
    List<Cart> findAllByQqNumber(String qqNumber);
    Cart findByQqNumberAndGoodsId(String qq,Long id);
    @Modifying
    @Transactional
    void deleteByQqNumberAndGoodsId(String qqNumber, Long goodsId);
    List<Cart> findByQqNumber(String qqNumber);

    List<Cart> findAll();

    @Modifying
    @Transactional
    void deleteByQqNumber(String qqNumber);

}
