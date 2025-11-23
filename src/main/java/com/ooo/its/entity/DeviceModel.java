package com.ooo.its.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "device_models")
public class DeviceModel {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long id;

    @Column(name = "device_model")
    private String deviceModel;

    @Column(name = "model_code")
    private String modelCode;

    public DeviceModel(){}
    public DeviceModel(Long id,String deviceModel,String modelCode){
        this.id = id;
        this.deviceModel = deviceModel;
        this.modelCode = modelCode;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }
}
