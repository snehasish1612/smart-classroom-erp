package com.smartclassroom.erp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "authorities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String designation;

    private String phone;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Authority oversees ALL streams
    @OneToMany
    @JoinTable(
        name = "authority_streams",
        joinColumns = @JoinColumn(name = "authority_id"),
        inverseJoinColumns = @JoinColumn(name = "stream_id")
    )
    private List<Stream> overseesStreams;

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }
}