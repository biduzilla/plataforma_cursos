package org.acme.plataforma.courses.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "module")
public class Module {
    @Id
    @GeneratedValue
    public UUID id;

    @Column(nullable = false)
    public String title;

    @Column(name = "module_order", nullable = false)
    public Integer order;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    public Course course;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order ASC")
    public List<Lesson> lessons = new ArrayList<>();
}
