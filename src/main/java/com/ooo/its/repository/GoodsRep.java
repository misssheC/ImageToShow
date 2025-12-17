package com.ooo.its.repository;

import com.ooo.its.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface GoodsRep extends JpaRepository<Goods,Long> {
    List<Goods> findByParentFolderAndDeleteOrderByUpdateDesc(String parentfolder,int d);

    @Query("SELECT g FROM Goods g ORDER BY g.buy DESC limit 50")
    List<Goods> findAllOrderByBuyDesc();

    List<Goods> findAllByUpdateGreaterThan(Date Time);
}
