package com.ooo.its.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, name = "order_id")
    private Long id;
    @Column(name = "qq_number")
    private String qqNumber;
    @Column(name = "order_date")
    private Date date;
    @Column(name = "goods_id")
    private Long goodsId;
    @Column(name = "order_state")
    private int state;
    @Column(name = "batch")
    private int batch;
    @Column(name = "is_load")
    private int is_load = 0;

    @Column(name = "is_ship")
    private int is_ship = 0;
    public Order(){}
    public Order(Long id,String qqNumber,Date date,Long goodsId,int state,int batch,int is_load,int is_ship){
        this.id = id;
        this.qqNumber = qqNumber;
        this.date = date;
        this.goodsId = goodsId;
        this.state = state;
        this.batch = batch;
        this.is_load = is_load;
        this.is_ship = is_ship;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

    public int getIs_load() {
        return is_load;
    }

    public void setIs_load(int is_load) {
        this.is_load = is_load;
    }

    public int getIs_ship() {
        return is_ship;
    }

    public void setIs_ship(int is_ship) {
        this.is_ship = is_ship;
    }
}
