package com.smartclassroom.erp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "sections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer semester;

    // Many Sections belong to One Stream
    @ManyToOne
    @JoinColumn(name = "stream_id", nullable = false)
    @JsonBackReference
    private Stream stream;

    // One Section has many Students
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Student> students;

    // One Section has many Attendance records
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Attendance> attendances;
}