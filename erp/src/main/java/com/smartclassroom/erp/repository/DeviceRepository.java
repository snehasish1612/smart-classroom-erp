package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByClassroomId(Long classroomId);
    List<Device> findByStatus(String status);
    List<Device> findByDeviceName(String deviceName);
    Boolean existsByDeviceNameAndClassroomId(String deviceName, Long classroomId);
}