package com.ooo.its.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "daily")
public class Daily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "day_id")
    private Long id;
    @Column(name = "date")
    private String date;
    @Column(name = "login_number")
    private int login;
    @Column(name = "view_number")
    private int view;
    @Column(name = "cart")
    private int cart;
    @Column(name = "income")
    private int income;
    @Column(name = "login_error")
    private int lerror;
    @Column(name = "region_error")
    private int rerror;

    public Daily(){}
    public Daily(Long id,String date,int login,int view,int cart ,int income,int lerror,int rerror){
        this.date = date;
        this.login = login;
        this.view = view;
        this.cart = cart;
        this.income = income;
        this.lerror = lerror;
        this.rerror = rerror;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getLerror() {
        return lerror;
    }

    public void setLerror(int lerror) {
        this.lerror = lerror;
    }

    public int getRerror() {
        return rerror;
    }

    public void setRerror(int rerror) {
        this.rerror = rerror;
    }

    public int getCart() {
        return cart;
    }

    public void setCart(int cart) {
        this.cart = cart;
    }
}
