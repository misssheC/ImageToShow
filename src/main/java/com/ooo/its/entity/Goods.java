package com.ooo.its.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "goods_info")
public class Goods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goods_id")
    private Long id;
    @Column(name = "goods_name")
    private String name;
    @Column(name = "parent_folder")
    private String parentFolder;

    @Column(name = "click")
    private int click;
    @Column(name = "buy")
    private int buy;
    @Column(name = "url")
    private String url;
    @Column(name = "update_time")
    private Date update;

    @Column(name = "is_deleted")
    private int delete;
    public Goods() {}
    public Goods(Long id,String goodsName, String parentFolder,int click ,int buy,String url,Date update,int delete) {
        this.id = id;
        this.name = goodsName;
        this.parentFolder = parentFolder;
        this.click = click;
        this.buy = buy;
        this.url = url;
        this.update = update;
        this.delete = delete;
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

    public String getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(String parentFolder) {
        this.parentFolder = parentFolder;
    }

    public int getClick() {
        return click;
    }

    public void setClick(int click) {
        this.click = click;
    }

    public int getBuy() {
        return buy;
    }

    public void setBuy(int buy) {
        this.buy = buy;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }

    public int getDelete() {
        return delete;
    }

    public void setDelete(int delete) {
        this.delete = delete;
    }
}
