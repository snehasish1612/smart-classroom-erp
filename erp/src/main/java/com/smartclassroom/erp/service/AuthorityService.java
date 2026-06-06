package com.smartclassroom.erp.service;

import com.smartclassroom.erp.entity.Authority;
import com.smartclassroom.erp.entity.Stream;
import com.smartclassroom.erp.repository.AuthorityRepository;
import com.smartclassroom.erp.repository.StreamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private StreamRepository streamRepository;

    // Get all authorities
    public List<Authority> getAllAuthorities() {
        return authorityRepository.findAll();
    }

    // Get authority by id
    public Optional<Authority> getAuthorityById(Long id) {
        return authorityRepository.findById(id);
    }

    // Get authority by email
    public Optional<Authority> getAuthorityByEmail(String email) {
        return authorityRepository.findByEmail(email);
    }

    // Create new authority
    public Authority createAuthority(Authority authority) {

        // Business logic 1:
        // Check if email already exists
        if (authorityRepository.existsByEmail(authority.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // Business logic 2:
        // Only ONE authority should exist
        if (authorityRepository.count() > 0) {
            throw new RuntimeException(
                "Authority already exists! " +
                "System can only have one Authority.");
        }

        return authorityRepository.save(authority);
    }

    // Update authority
    public Authority updateAuthority(Long id, Authority updatedAuthority) {
        Authority existing = authorityRepository.findById(id)
            .orElseThrow(() -> 
                new RuntimeException("Authority not found!"));

        existing.setName(updatedAuthority.getName());
        existing.setEmail(updatedAuthority.getEmail());
        existing.setDesignation(updatedAuthority.getDesignation());
        existing.setPhone(updatedAuthority.getPhone());

        return authorityRepository.save(existing);
    }

    // Get all streams authority oversees
    public List<Stream> getOverseenStreams(Long authorityId) {
        Authority authority = authorityRepository.findById(authorityId)
            .orElseThrow(() -> 
                new RuntimeException("Authority not found!"));
        return authority.getOverseesStreams();
    }

    // Add stream to authority oversight
    public Authority addStreamToOversight(
            Long authorityId, Long streamId) {

        Authority authority = authorityRepository.findById(authorityId)
            .orElseThrow(() -> 
                new RuntimeException("Authority not found!"));

        Stream stream = streamRepository.findById(streamId)
            .orElseThrow(() -> 
                new RuntimeException("Stream not found!"));

        // Check if already overseeing
        if (authority.getOverseesStreams().contains(stream)) {
            throw new RuntimeException(
                "Authority already oversees this stream!");
        }

        authority.getOverseesStreams().add(stream);
        return authorityRepository.save(authority);
    }

    // Remove stream from authority oversight
    public Authority removeStreamFromOversight(
            Long authorityId, Long streamId) {

        Authority authority = authorityRepository.findById(authorityId)
            .orElseThrow(() -> 
                new RuntimeException("Authority not found!"));

        Stream stream = streamRepository.findById(streamId)
            .orElseThrow(() -> 
                new RuntimeException("Stream not found!"));

        authority.getOverseesStreams().remove(stream);
        return authorityRepository.save(authority);
    }

    // Delete authority
    public void deleteAuthority(Long id) {
        if (!authorityRepository.existsById(id)) {
            throw new RuntimeException("Authority not found!");
        }
        authorityRepository.deleteById(id);
    }
}