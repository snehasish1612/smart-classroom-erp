package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.entity.Authority;
import com.smartclassroom.erp.entity.Stream;
import com.smartclassroom.erp.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/authority")
public class AuthorityController {

    @Autowired
    private AuthorityService authorityService;

    // GET all authorities
    @GetMapping
    public ResponseEntity<List<Authority>> getAllAuthorities() {
        return ResponseEntity.ok(
            authorityService.getAllAuthorities());
    }

    // GET authority by id
    @GetMapping("/{id}")
    public ResponseEntity<Authority> getAuthorityById(
            @PathVariable Long id) {
        return authorityService.getAuthorityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET authority by email
    @GetMapping("/email/{email}")
    public ResponseEntity<Authority> getAuthorityByEmail(
            @PathVariable String email) {
        return authorityService.getAuthorityByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET all streams authority oversees
    @GetMapping("/{id}/streams")
    public ResponseEntity<List<Stream>> getOverseenStreams(
            @PathVariable Long id) {
        return ResponseEntity.ok(
            authorityService.getOverseenStreams(id));
    }

    // POST - create authority
    @PostMapping
    public ResponseEntity<Authority> createAuthority(
            @RequestBody Authority authority) {
        Authority saved = authorityService
            .createAuthority(authority);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(saved);
    }

    // PUT - update authority
    @PutMapping("/{id}")
    public ResponseEntity<Authority> updateAuthority(
            @PathVariable Long id,
            @RequestBody Authority authority) {
        Authority updated = authorityService
            .updateAuthority(id, authority);
        return ResponseEntity.ok(updated);
    }

    // PUT - add stream to oversight
    @PutMapping("/{authorityId}/streams/{streamId}")
    public ResponseEntity<Authority> addStreamToOversight(
            @PathVariable Long authorityId,
            @PathVariable Long streamId) {
        Authority authority = authorityService
            .addStreamToOversight(authorityId, streamId);
        return ResponseEntity.ok(authority);
    }

    // DELETE - remove stream from oversight
    @DeleteMapping("/{authorityId}/streams/{streamId}")
    public ResponseEntity<Authority> removeStreamFromOversight(
            @PathVariable Long authorityId,
            @PathVariable Long streamId) {
        Authority authority = authorityService
            .removeStreamFromOversight(authorityId, streamId);
        return ResponseEntity.ok(authority);
    }

    // DELETE - delete authority
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuthority(
            @PathVariable Long id) {
        authorityService.deleteAuthority(id);
        return ResponseEntity.ok("Authority deleted successfully!");
    }
}