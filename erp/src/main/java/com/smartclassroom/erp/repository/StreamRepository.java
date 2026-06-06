package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StreamRepository extends JpaRepository<Stream, Long> {

    // Find stream by name
    Optional<Stream> findByName(String name);

    // Check if stream exists by name
    Boolean existsByName(String name);

    // Search streams by name containing keyword
    List<Stream> findByNameContaining(String keyword);
}