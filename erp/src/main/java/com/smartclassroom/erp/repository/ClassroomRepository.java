package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    
    // Find by status
List<Classroom> findByStatus(Classroom.Status status);
    
    // Find by building
    List<Classroom> findByBuilding(String building);
    
    // Find by room number
    Optional<Classroom> findByRoomNumber(String roomNumber);
    
    // Check if room number exists
    boolean existsByRoomNumber(String roomNumber);
}