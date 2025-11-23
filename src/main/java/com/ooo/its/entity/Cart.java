package com.ooo.its.entity;

import jakarta.persistence.*;
import java.util.Date;


@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;
    @Column(name = "qq_number")
    private String qqNumber;
    @Column(name = "goods_id")
    private Long goodsId;
    @Column(name = "put_time")
    private Date time;
    @Column(name = "state")
    private int state;
    public Cart(){}
    public Cart(Long Id,String qqNumber,Long goodsId,Date time,int state){
        this.id = Id;
        this.qqNumber = qqNumber;
        this.goodsId = goodsId;
        this.time = time;
        this.state = state;
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

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
