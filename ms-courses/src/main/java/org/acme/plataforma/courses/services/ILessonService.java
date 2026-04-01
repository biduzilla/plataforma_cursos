package org.acme.plataforma.courses.services;

import jakarta.transaction.Transactional;
import org.acme.plataforma.courses.requests.LessonRequest;
import org.acme.plataforma.courses.requests.ModuleRequest;
import org.acme.plataforma.courses.responses.LessonResponse;
import org.acme.plataforma.courses.responses.ModuleResponse;

import java.util.UUID;

public interface ILessonService {
    ModuleResponse addModule(UUID courseId, ModuleRequest req, UUID instructorId);

    LessonResponse addLesson(UUID moduleId, LessonRequest req, UUID instructorId);

    void deleteLesson(UUID lessonId, UUID instructorId);
}
