package com.ooo.its.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "goods_key")
public class GoodsKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    private Long key_id;

    @Column(name = "qq_number", nullable = false, length = 20)
    private String qqNumber;

    @Column(name = "batch")
    private int batch;
    @Column(name = "message_id")
    private String messageId;
    @Column(name = "file_name")
    private String filename;
    public GoodsKey() {
    }

    public GoodsKey(String qqNumber, int batch,String messageId,String filename) {
        this.qqNumber = qqNumber;
        this.batch = batch;
        this.messageId = messageId;
        this.filename = filename;
    }

    public Long getId() {
        return key_id;
    }

    public void setId(Long id) {
        this.key_id = id;
    }

    public String getQqNumber() {
        return qqNumber;
    }

    public void setQqNumber(String qqNumber) {
        this.qqNumber = qqNumber;
    }

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
