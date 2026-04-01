package org.acme.plataforma.courses.services.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.plataforma.courses.exceptions.ForbiddenException;
import org.acme.plataforma.courses.exceptions.NotFoundException;
import org.acme.plataforma.courses.mapper.CourseMapper;
import org.acme.plataforma.courses.models.Lesson;
import org.acme.plataforma.courses.models.Module;
import org.acme.plataforma.courses.repositories.impl.LessonRepository;
import org.acme.plataforma.courses.repositories.impl.ModuleRepository;
import org.acme.plataforma.courses.requests.LessonRequest;
import org.acme.plataforma.courses.requests.ModuleRequest;
import org.acme.plataforma.courses.responses.LessonResponse;
import org.acme.plataforma.courses.responses.ModuleResponse;
import org.acme.plataforma.courses.services.ILessonService;

import java.util.UUID;

@ApplicationScoped
public class LessonService implements ILessonService {
    @Inject
    ModuleRepository moduleRepository;

    @Inject
    LessonRepository lessonRepository;

    @Inject
    CourseService courseService;

    @Inject
    CourseMapper mapper;

    @Override
    @Transactional
    public ModuleResponse addModule(UUID courseId, ModuleRequest req, UUID instructorId) {
        var course = courseService.findById(courseId);

        if (!course.instructorId.equals(instructorId)) {
            throw new ForbiddenException("Sem permissão");
        }
        Module module = new Module();
        module.title = req.title();
        module.order = req.order();
        module.course = course;

        moduleRepository.persist(module);
        return mapper.toModuleResponse(module);
    }

    @Override
    @Transactional
    public LessonResponse addLesson(UUID moduleId, LessonRequest req, UUID instructorId) {
        Module module = moduleRepository.findByIdOptional(moduleId)
                .orElseThrow(() -> new NotFoundException(moduleId));
        if (!module.course.instructorId.equals(instructorId)) {
            throw new ForbiddenException("Sem permissão");
        }

        var lesson = new Lesson();
        lesson.title = req.title();
        lesson.videoUrl = req.videoUrl();
        lesson.durationSeconds = req.durationSeconds();
        lesson.order = req.order();
        lesson.module = module;

        lessonRepository.persist(lesson);
        return mapper.toLessonResponse(lesson);
    }

    @Override
    @Transactional
    public void deleteLesson(UUID lessonId, UUID instructorId) {
        Lesson lesson = lessonRepository.findByIdOptional(lessonId)
                .orElseThrow(() -> new NotFoundException(lessonId));

        if (!lesson.module.course.instructorId.equals(instructorId)) {
            throw new ForbiddenException("Sem permissão");
        }

        lessonRepository.delete(lesson);
    }
}
