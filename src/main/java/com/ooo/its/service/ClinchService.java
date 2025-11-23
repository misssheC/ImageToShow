package com.ooo.its.service;

import com.ooo.its.entity.Record;
import com.ooo.its.repository.RecordRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ClinchService {
    @Autowired
    RecordRep recordRep;
    public boolean SaveRecord(String qq,int goodsQuantity,int amount){
        int batch = recordRep.findMaxBatchByQq(qq) + 1;
        Record record = new Record();
        record.setQqNumber(qq);
        record.setAmount(amount);
        record.setGoodsQuantity(goodsQuantity);
        record.setTime(new Date());
        record.setBatch(batch);
        try {
            recordRep.save(record);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
