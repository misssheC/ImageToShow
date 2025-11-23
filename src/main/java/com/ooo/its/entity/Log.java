package com.ooo.its.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "log_record")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;
    @Column(name = "log_time")
    private Date time;
    @Column(name = "log_user")
    private String user;
    @Column(name = "log_event",length = 1000)
    private String event;
    @Column(name = "log_type")
    private int type;

    public Log(){}

    public Log(Long id,Date time,String user,String event,int type){
        this.id = id;
        this.time = time;
        this.user = user;
        this.event = event;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
