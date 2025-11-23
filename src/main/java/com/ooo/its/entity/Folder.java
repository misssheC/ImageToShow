package com.ooo.its.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "folder_info")
public class Folder {
    @Id
    @Column(name = "folder_id")
    private Long id;

    @Column(name = "folder_name")
    private String name;

    @Column(name = "total_quantity")
    private int quantity;
    @Column(name = "last_update_quantity")
    private int last;

    public Folder() {}
    public Folder(Long id,String folderName, int quantity,int last) {
        this.id = id;
        this.name = folderName;
        this.quantity = quantity;
        this.last = last;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }
}
