package com.ooo.its.service;

import com.ooo.its.entity.GoodsKey;
import com.ooo.its.repository.GoodsKeyRep;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoodsKeyService {

    @Resource
    private GoodsKeyRep goodsKeyRep;
    public List<GoodsKey> findByQqNumberAndBatch(String qqNumber, int batch) {
        return goodsKeyRep.findByQqNumberAndBatch(qqNumber, batch);
    }
    public GoodsKey save(GoodsKey goodsKey) {
        return goodsKeyRep.save(goodsKey);
    }
}

