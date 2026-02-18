package com.ooo.its.repository;

import org.springframework.data.domain.Pageable;
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
    List<Cart> findAllByQqNumberOrderByIdDesc(String qqNumber,Pageable pageable);

    List<Cart> findAllByOrderByIdDesc();

    List<Cart> findByStateOrderByIdDesc(int state,Pageable pageable);

    List<Cart> findByQqNumberAndStateOrderByIdDesc(String qq , int state,Pageable pageable );

    @Modifying
    @Transactional
    void deleteByQqNumberAndState(String qqNumber,int state);

    @Modifying
    @Transactional
    void deleteById(@NonNull Long id);


}
