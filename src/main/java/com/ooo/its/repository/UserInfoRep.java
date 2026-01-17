package com.ooo.its.repository;
import com.ooo.its.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserInfoRep extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByQqNumber(String qqNumber);

    Page<UserInfo> findAllByOrderByLastTimeDesc(Pageable pageable);

    List<UserInfo> findAllByOrderByLumpSumDesc();

    @Query("SELECT COALESCE(SUM(r.lumpSum), 0) FROM UserInfo r")
    int sumTotalLumpSum();

    @Query("SELECT COALESCE(SUM(r.purchaseQuantity), 0) FROM UserInfo r")
    int sumTotalPurchaseQuantity();

    @Modifying
    @Transactional
    void deleteByQqNumber(String qqNumber);

    List<UserInfo> findAllByNumberOfLoginsAndPurchaseQuantity(int n,int p);
}
