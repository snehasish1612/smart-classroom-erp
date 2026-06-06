package com.smartclassroom.erp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "streams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    // One Stream has many Sections
    @OneToMany(mappedBy = "stream", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Section> sections;
}