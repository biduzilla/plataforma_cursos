package org.acme.plataforma.courses.repositories.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.plataforma.courses.models.Lesson;
import org.acme.plataforma.courses.repositories.ILessonRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class LessonRepository implements ILessonRepository {
    @Override
    public List<Lesson> findByModule(UUID moduleId) {
        return find("module.id", moduleId).list();
    }
}
