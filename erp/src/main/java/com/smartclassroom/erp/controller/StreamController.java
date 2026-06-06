package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.entity.Stream;
import com.smartclassroom.erp.service.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/streams")
public class StreamController {

    @Autowired
    private StreamService streamService;

    // GET all streams
    @GetMapping
    public ResponseEntity<List<Stream>> getAllStreams() {
        return ResponseEntity.ok(streamService.getAllStreams());
    }

    // GET stream by id
    @GetMapping("/{id}")
    public ResponseEntity<Stream> getStreamById(
            @PathVariable Long id) {
        return streamService.getStreamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET stream by name
    @GetMapping("/name/{name}")
    public ResponseEntity<Stream> getStreamByName(
            @PathVariable String name) {
        return streamService.getStreamByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET search streams
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<Stream>> searchStreams(
            @PathVariable String keyword) {
        return ResponseEntity.ok(
            streamService.searchStreams(keyword));
    }

    // POST - create stream
    @PostMapping
    public ResponseEntity<Stream> createStream(
            @RequestBody Stream stream) {
        Stream saved = streamService.createStream(stream);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT - update stream
    @PutMapping("/{id}")
    public ResponseEntity<Stream> updateStream(
            @PathVariable Long id,
            @RequestBody Stream stream) {
        Stream updated = streamService.updateStream(id, stream);
        return ResponseEntity.ok(updated);
    }

    // DELETE - delete stream
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStream(
            @PathVariable Long id) {
        streamService.deleteStream(id);
        return ResponseEntity.ok("Stream deleted successfully!");
    }
}