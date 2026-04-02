package org.acme.plataforma.courses.services.impl;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.plataforma.courses.enums.CourseStatusEnum;
import org.acme.plataforma.courses.exceptions.BadRequestException;
import org.acme.plataforma.courses.exceptions.ForbiddenException;
import org.acme.plataforma.courses.exceptions.NotFoundException;
import org.acme.plataforma.courses.mapper.CourseMapper;
import org.acme.plataforma.courses.models.Course;
import org.acme.plataforma.courses.producers.CourseEventProducer;
import org.acme.plataforma.courses.repositories.ICourseRepository;
import org.acme.plataforma.courses.requests.CourseRequest;
import org.acme.plataforma.courses.responses.CourseResponse;
import org.acme.plataforma.courses.responses.PageResponse;
import org.acme.plataforma.courses.services.ICourseService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CourseService implements ICourseService {
    @Inject
    ICourseRepository courseRepository;
    @Inject
    CourseMapper courseMapper;
    @Inject
    CourseEventProducer eventProducer;

    @Override
    public PageResponse<CourseResponse> search(String title, CourseStatusEnum status, int page, int pageSize) {
        PanacheQuery<Course> query = courseRepository
                .search(title, status)
                .page(page, pageSize);
        List<CourseResponse> content = query.list()
                .stream()
                .map(courseMapper::toResponse)
                .toList();
        return new PageResponse<>(content, query.count(), page, pageSize);
    }

    @Override
    public Course findById(UUID id) {
        return courseRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    @Transactional
    public CourseResponse create(CourseRequest req, UUID instructorId) {
        var c = new Course();
        c.title = req.title();
        c.description = req.description();
        c.price = req.price();
        c.instructorId = instructorId;
        c.status = CourseStatusEnum.DRAFT;

        courseRepository.persist(c);
        return courseMapper.toResponse(c);
    }

    @Override
    @Transactional
    public CourseResponse update(UUID id, CourseRequest req, UUID instructorId) {
        var course = findById(id);
        validateOwnership(course, instructorId);
        if (course.status == CourseStatusEnum.ARCHIVED) {
            throw new BadRequestException("Curso arquivado não pode ser editado");
        }

        course.title = req.title();
        course.description = req.description();
        course.price = req.price();
        course.updatedAt = LocalDateTime.now();

        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional
    public CourseResponse publish(UUID id, UUID instructorId) {
        var course = findById(id);
        validateOwnership(course, instructorId);
        if (course.status == CourseStatusEnum.ARCHIVED) {
            throw new BadRequestException("Curso arquivado não pode ser editado");
        }

        var hasContent = course.modules.stream()
                .anyMatch(m -> !m.lessons.isEmpty());

        if (course.modules.isEmpty() || !hasContent) {
            throw new BadRequestException(
                    "Curso precisa ter ao menos 1 módulo com 1 aula para ser publicado"
            );
        }

        course.status = CourseStatusEnum.PUBLISHED;
        course.updatedAt = LocalDateTime.now();

        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional
    public CourseResponse archive(UUID id, UUID instructorId) {
        var course = findById(id);
        validateOwnership(course, instructorId);
        if (course.status == CourseStatusEnum.ARCHIVED) {
            throw new BadRequestException("Curso arquivado não pode ser editado");
        }

        course.status = CourseStatusEnum.ARCHIVED;
        course.updatedAt = LocalDateTime.now();

        eventProducer.publishCourseArchived(course);

        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID instructorId) {
        var course = findById(id);
        validateOwnership(course, instructorId);
        if (course.status == CourseStatusEnum.ARCHIVED) {
            throw new BadRequestException("Curso arquivado não pode ser editado");
        }

        if (course.status == CourseStatusEnum.PUBLISHED) {
            throw new BadRequestException(
                    "Curso publicado não pode ser deletado, apenas arquivado"
            );
        }

        courseRepository.delete(course);
    }

    private void validateOwnership(Course course, UUID instructorId) {
        if (!course.instructorId.equals(instructorId)) {
            throw new ForbiddenException("Você não tem permissão para editar este curso");
        }
    }
}
