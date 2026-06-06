package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AuthorityRepository 
        extends JpaRepository<Authority, Long> {

    // Find authority by email
    Optional<Authority> findByEmail(String email);

    // Check if email exists
    Boolean existsByEmail(String email);

    // Find authority by designation
    Optional<Authority> findByDesignation(String designation);
}