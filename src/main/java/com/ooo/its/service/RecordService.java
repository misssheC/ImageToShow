package com.ooo.its.service;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import com.ooo.its.entity.Record;
import com.ooo.its.repository.RecordRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RecordService {

    @Autowired
    RecordRep recordRep;

    public int FindBatch(String qq){

        return recordRep.findMaxBatchByQq(qq);
    }

    public boolean RecordTransaction(String qq,int amount,int quantity,int batch){
        Record record = new Record();
        record.setQqNumber(qq);
        record.setTime(new Date());
        record.setAmount(amount);
        record.setGoodsQuantity(quantity);
        record.setBatch(batch);
        try {
            recordRep.save(record);
            return true;
        }
        catch (Exception e){
            System.out.println(e);
            return false;
        }
    }

    public Record FindUnPayOrder(String qq , int batch , int ispay){
        return recordRep.findByQqNumberAndBatchAndIsPay(qq, batch, ispay);
    }

    public boolean SaveRecord(Record record){

        recordRep.save(record);
        return true;
    }

    public List<Record> FindAllRecords(){
        return recordRep.findAllByOrderByIdDesc();
    }
}
