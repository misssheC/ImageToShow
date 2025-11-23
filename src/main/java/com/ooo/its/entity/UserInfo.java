package com.ooo.its.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_info")
public class UserInfo {
    @Id
    @Column(unique = true, nullable = false, name = "qq_number")
    private String qqNumber;
    @Column(name = "last_time")
    private Date lastTime;
    @Column(name = "reg_time")
    private Date regTime;
    @Column(name = "purchase_quantity")
    private int purchaseQuantity;
    @Column(name = "number_of_logins")
    private int numberOfLogins;
    @Column(name = "model",length = 1000)
    private String model;
    @Column(name = "lump_sum")
    private int lumpSum;
    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "device")
    private String device;
    @Column(name = "region")
    private String region;

    @Column(name = "to_update_time")
    private Date update;
    public UserInfo() {}
    public UserInfo(String qqNumber,Date lastTime,Date regTime,int purchaseQuantity,int numberOfLogins,String model,int lumpSum,String IP,String device,String region,Date update) {
        this.qqNumber = qqNumber;
        this.lastTime = lastTime;
        this.regTime = regTime;
        this.purchaseQuantity = purchaseQuantity;
        this.numberOfLogins = numberOfLogins;
        this.model = model;
        this.lumpSum = lumpSum;
        this.ipAddress = IP;
        this.device = device;
        this.region = region;
        this.update = update;
    }

    public String getIpAddress(){
        return ipAddress;
    }
    public String getQqNumber() {
        return qqNumber;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public Date getRegTime() {
        return regTime;
    }

    public int getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public int getNumberOfLogins() {
        return numberOfLogins;
    }

    public String getModel() {
        return model;
    }

    public int getLumpSum() {
        return lumpSum;
    }

    public void setqqNumber(String QqNumber) {
        qqNumber = QqNumber;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPurchaseQuantity(int PurchaseQuantity) {
        purchaseQuantity = PurchaseQuantity;
    }

    public void setLumpSum(int LumpSum) {
        lumpSum = LumpSum;
    }

    public void setLastTime(Date LastTime) {
        lastTime = LastTime;
    }

    public void setModel(String Model) {
        model = Model;
    }

    public void setNumberOfLogins(int NumberOfLogins) {
        numberOfLogins = NumberOfLogins;
    }

    public void setRegTime(Date RegTime) {
        regTime = RegTime;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }
}
