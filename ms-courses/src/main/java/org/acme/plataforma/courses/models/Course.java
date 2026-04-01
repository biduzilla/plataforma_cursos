package org.acme.plataforma.courses.models;

import jakarta.persistence.*;
import org.acme.plataforma.courses.enums.CourseStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(nullable = false)
    public String title;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Column(nullable = false)
    public BigDecimal price;

    @Column(nullable = false)
    public UUID instructorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public CourseStatusEnum status = CourseStatusEnum.DRAFT;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order ASC")
    public List<Module> modules = new ArrayList<>();

    public LocalDateTime createdAt = LocalDateTime.now();
    public LocalDateTime updatedAt = LocalDateTime.now();
}
