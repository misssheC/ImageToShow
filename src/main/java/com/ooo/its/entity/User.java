package com.ooo.its.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_login")
public class User {
    @Id
    @Column(unique = true, nullable = false,name = "qq_number")
    private String qqNumber;

    @Column(name = "password")
    private String password = "111222";
    @Column(name = "state")
    private int state;
    @Column(name = "vip")
    private int vip;
    @Column(name = "black")
    private int black;
    @Column(name = "publicity")
    private int publicity;
    public User() {}
    public User(String qqnumber, String password,int state,int vip,int black,int publicity) {
        this.qqNumber = qqnumber;
        this.password = password;
        this.state = state;
        this.black = black;
        this.vip = vip;
        this.publicity = publicity;
    }
    public String getPassword() {
        return password;
    }

    public void setQqnumber(String qqnumber) {
        this.qqNumber = qqnumber;
    }

    public String getQqnumber() {
        return qqNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getBlack() {
        return black;
    }

    public void setBlack(int black) {
        this.black = black;
    }

    public int getPublicity() {
        return publicity;
    }

    public void setPublicity(int publicity) {
        this.publicity = publicity;
    }
}
