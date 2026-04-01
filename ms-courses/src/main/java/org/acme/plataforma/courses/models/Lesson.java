package org.acme.plataforma.courses.models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(nullable = false)
    public String title;

    @Column(name = "video_url")
    public String videoUrl;

    @Column(name = "duration_seconds")
    public Integer durationSeconds;

    @Column(name = "lesson_order", nullable = false)
    public Integer order;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    public Module module;
}
