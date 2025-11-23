package com.ooo.its.repository;

import com.ooo.its.entity.DeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeviceModelRep extends JpaRepository<DeviceModel, Long> {
    @Query("SELECT dm FROM DeviceModel dm WHERE dm.modelCode = :modelCode")
    List<DeviceModel> findByModelCode(@Param("modelCode") String modelCode);
    @Query("SELECT dm FROM DeviceModel dm WHERE dm.modelCode LIKE %:modelCode%")
    List<DeviceModel> findByModelCodeContaining(@Param("modelCode") String modelCode);
}
