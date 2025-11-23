package com.ooo.its.entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
@Table(name = "transaction_record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;
    @Column(name = "qq_number")
    private String qqNumber;
    @Column(name = "transaction_time")
    private Date time;
    @Column(name = "amount")
    private int amount;

    @Column(name = "goods_quantity")
    private int goodsQuantity;
    @Column(name = "batch")
    private int batch;

    @Column(name = "is_pay")
    private int isPay;
    public Record(){}
    public Record(Long id,String qqNumber,Date time,int amount,int batch,int isPay){
        this.id = id;
        this.qqNumber = qqNumber;
        this.time = time;
        this.amount = amount;
        this.batch = batch;
        this.isPay = isPay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQqNumber() {
        return qqNumber;
    }

    public void setQqNumber(String qqNumber) {
        this.qqNumber = qqNumber;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

    public int getGoodsQuantity() {
        return goodsQuantity;
    }

    public void setGoodsQuantity(int goodsQuantity) {
        this.goodsQuantity = goodsQuantity;
    }

    public int getIsPay() {
        return isPay;
    }

    public void setIsPay(int isPay) {
        this.isPay = isPay;
    }
}
