package com.ooo.its.repository;

import org.springframework.lang.NonNull;
import com.ooo.its.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartRep extends JpaRepository<Cart,Long> {
    Cart findByQqNumberAndGoodsId(String qq,Long id);
    @Modifying
    @Transactional
    void deleteByQqNumberAndGoodsId(String qqNumber, Long goodsId);
    List<Cart> findByQqNumberOrderByIdDesc(String qqNumber);

    List<Cart> findAllByOrderByIdDesc();

    List<Cart> findByStateOrderByIdDesc(int state);

    List<Cart> findByQqNumberAndStateOrderByIdDesc(String qq , int state);

    @Modifying
    @Transactional
    void deleteByQqNumber(String qqNumber);

    @Modifying
    @Transactional
    void deleteById(@NonNull Long id);


}
