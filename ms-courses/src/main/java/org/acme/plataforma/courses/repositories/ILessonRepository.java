package org.acme.plataforma.courses.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.acme.plataforma.courses.models.Lesson;

import java.util.List;
import java.util.UUID;

public interface ILessonRepository extends PanacheRepositoryBase<Lesson, UUID> {
    public List<Lesson> findByModule(UUID moduleId);
}
