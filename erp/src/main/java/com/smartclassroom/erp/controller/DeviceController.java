package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.entity.Device;
import com.smartclassroom.erp.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        return deviceService.getDeviceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<Device>> getByClassroom(
            @PathVariable Long classroomId) {
        return ResponseEntity.ok(
            deviceService.getDevicesByClassroom(classroomId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Device>> getByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(deviceService.getDevicesByStatus(status));
    }

    @PostMapping
    public ResponseEntity<Device> createDevice(
            @Valid @RequestBody Device device) {
        Device saved = deviceService.saveDevice(device);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}/on")
    public ResponseEntity<Device> turnOn(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.turnOn(id));
    }

    @PutMapping("/{id}/off")
    public ResponseEntity<Device> turnOff(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.turnOff(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.ok("Device deleted successfully!");
    }
}