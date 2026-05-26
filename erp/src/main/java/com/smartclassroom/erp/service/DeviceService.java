package com.smartclassroom.erp.service;

import com.smartclassroom.erp.config.ResourceNotFoundException;
import com.smartclassroom.erp.entity.Device;
import com.smartclassroom.erp.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    // GET ALL
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    // GET BY ID
    public Optional<Device> getDeviceById(Long id) {
        return deviceRepository.findById(id);
    }

    // GET BY CLASSROOM
    public List<Device> getDevicesByClassroom(Long classroomId) {
        return deviceRepository.findByClassroomId(classroomId);
    }

    // GET BY STATUS
    public List<Device> getDevicesByStatus(String status) {
        return deviceRepository.findByStatus(status);
    }

    // CREATE
    public Device saveDevice(Device device) {

        if (deviceRepository.existsByDeviceNameAndClassroomId(
                device.getDeviceName(),
                device.getClassroomId())) {
            throw new RuntimeException("Device already exists in this classroom!");
        }

        return deviceRepository.save(device);
    }

    // TURN ON
    public Device turnOn(Long id) {

        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found!"));

        device.setStatus("ON");
        return deviceRepository.save(device);
    }

    // TURN OFF
    public Device turnOff(Long id) {

        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found!"));

        device.setStatus("OFF");
        return deviceRepository.save(device);
    }

    // DELETE
    public void deleteDevice(Long id) {

        if (!deviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Device not found!");
        }

        deviceRepository.deleteById(id);
    }
}