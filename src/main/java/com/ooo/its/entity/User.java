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

    public User() {}
    public User(String qqnumber, String password,int state) {
        this.qqNumber = qqnumber;
        this.password = password;
        this.state = state;
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
}
