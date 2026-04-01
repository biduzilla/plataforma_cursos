package org.acme.plataforma.courses.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.plataforma.courses.models.Course;
import org.acme.plataforma.courses.models.Lesson;
import org.acme.plataforma.courses.models.Module;
import org.acme.plataforma.courses.responses.CourseResponse;
import org.acme.plataforma.courses.responses.LessonResponse;
import org.acme.plataforma.courses.responses.ModuleResponse;

@ApplicationScoped
public class CourseMapper {

    public CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.id,
                course.title,
                course.description,
                course.price,
                course.instructorId,
                course.status,
                course.modules.stream().map(this::toModuleResponse).toList(),
                course.createdAt
        );
    }

    public ModuleResponse toModuleResponse(Module module) {
        return new ModuleResponse(
                module.id,
                module.title,
                module.order,
                module.lessons.stream().map(this::toLessonResponse).toList()
        );
    }

    public LessonResponse toLessonResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.id,
                lesson.title,
                lesson.videoUrl,
                lesson.durationSeconds,
                lesson.order
        );
    }
}