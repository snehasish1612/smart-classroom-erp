package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository automatically gives you methods like save(), findAll(), findById(), and deleteById()!
}