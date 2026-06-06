package com.smartclassroom.erp.service;

import com.smartclassroom.erp.entity.Stream;
import com.smartclassroom.erp.repository.StreamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StreamService {

    @Autowired
    private StreamRepository streamRepository;

    // Get all streams
    public List<Stream> getAllStreams() {
        return streamRepository.findAll();
    }

    // Get stream by id
    public Optional<Stream> getStreamById(Long id) {
        return streamRepository.findById(id);
    }

    // Get stream by name
    public Optional<Stream> getStreamByName(String name) {
        return streamRepository.findByName(name);
    }

    // Search streams by keyword
    public List<Stream> searchStreams(String keyword) {
        return streamRepository.findByNameContaining(keyword);
    }

    // Create new stream
    public Stream createStream(Stream stream) {
        // Business logic:
        // Check if stream already exists
        if (streamRepository.existsByName(stream.getName())) {
            throw new RuntimeException(
                "Stream " + stream.getName() + " already exists!");
        }
        return streamRepository.save(stream);
    }

    // Update stream
    public Stream updateStream(Long id, Stream updatedStream) {
        Stream existing = streamRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Stream not found!"));

        existing.setName(updatedStream.getName());
        existing.setDescription(updatedStream.getDescription());

        return streamRepository.save(existing);
    }

    // Delete stream
    public void deleteStream(Long id) {
        if (!streamRepository.existsById(id)) {
            throw new RuntimeException("Stream not found!");
        }
        streamRepository.deleteById(id);
    }
}