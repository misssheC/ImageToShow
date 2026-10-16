package com.ooo.its.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "passwd")
public class Passwd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pd_id")
    private Long pd_id;
    @Column(name = "qq_number")
    private String qq;
    @Column(name = "batch")
    private int  batch;
    @Column(name = "password")
    private String password;
    @Column(name = "is_ok")
    private int ok;

    public Passwd(){}
    public Passwd(String qq,int batch,String password,int ok){
        this.qq = qq;
        this.batch = batch;
        this.password = password;
        this.ok = ok;
    }

    public Long getPd_id() {
        return pd_id;
    }

    public void setPd_id(Long pd_id) {
        this.pd_id = pd_id;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }
}

