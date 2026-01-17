package com.ooo.its.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "admin_login")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "passkey")
    private String passkey;
    @Column(name = "login_time")
    private Date time;
    @Column(name = "level")
    private int level;

    public Admin(Long id,String name,String passKey,Date loginTime,int level){
        this.id = id;
        this.name = name;
        this.passkey = passKey;
        this.time = loginTime;
        this.level = level;
    }

    public  Admin(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasskey() {
        return passkey;
    }

    public void setPasskey(String passkey) {
        this.passkey = passkey;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
