package org.acme.plataforma.courses.services;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.acme.plataforma.courses.models.Module;
import io.quarkus.test.InjectMock;
import org.acme.plataforma.courses.enums.CourseStatusEnum;
import org.acme.plataforma.courses.exceptions.BadRequestException;
import org.acme.plataforma.courses.exceptions.ForbiddenException;
import org.acme.plataforma.courses.exceptions.NotFoundException;
import org.acme.plataforma.courses.mapper.CourseMapper;
import org.acme.plataforma.courses.models.Course;
import org.acme.plataforma.courses.repositories.ICourseRepository;
import org.acme.plataforma.courses.requests.CourseRequest;
import org.acme.plataforma.courses.responses.CourseResponse;
import org.acme.plataforma.courses.responses.PageResponse;
import org.acme.plataforma.courses.services.impl.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CourseServiceTest {
    @InjectMocks
    CourseService service;

    @Mock
    ICourseRepository repository;

    @Mock
    CourseMapper mapper;

    @Mock
    PanacheQuery<Course> query;

    UUID instructorId;
    UUID otherInstructorId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        instructorId = UUID.randomUUID();
        otherInstructorId = UUID.randomUUID();
    }

    @Test
    void shouldSearchCourses(){
        Course course = new Course();
        CourseResponse response = mock(CourseResponse.class);

        when(repository.search(null, null)).thenReturn(query);
        when(query.page(0, 10)).thenReturn(query);
        when(query.list()).thenReturn(List.of(course));
        when(query.count()).thenReturn(1L);
        when(mapper.toResponse(course)).thenReturn(response);

        PageResponse<CourseResponse> result = service.search(null, null, 0, 10);

        assertEquals(1, result.content().size());
        assertEquals(1L, result.totalElements());
    }

    @Test
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        Course course = new Course();

        when(repository.findByIdOptional(id)).thenReturn(Optional.of(course));

        Course result = service.findById(id);

        assertNotNull(result);
    }

    @Test
    void shouldThrowNotFoundWhenCourseNotExists() {
        UUID id = UUID.randomUUID();

        when(repository.findByIdOptional(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(id));
    }

    // =========================
    // CREATE
    // =========================
    @Test
    void shouldCreateCourse() {
        CourseRequest req = new CourseRequest("title", "desc", BigDecimal.TEN);
        CourseResponse response = mock(CourseResponse.class);

        when(mapper.toResponse(any())).thenReturn(response);

        CourseResponse result = service.create(req, instructorId);

        assertNotNull(result);
        verify(repository).persist(any(Course.class));
    }

    // =========================
    // UPDATE
    // =========================
    @Test
    void shouldUpdateCourse() {
        UUID id = UUID.randomUUID();
        Course course = createCourse(instructorId, CourseStatusEnum.DRAFT);

        when(repository.findByIdOptional(id)).thenReturn(Optional.of(course));
        when(mapper.toResponse(course)).thenReturn(mock(CourseResponse.class));

        CourseRequest req = new CourseRequest("new", "desc", BigDecimal.ONE);

        CourseResponse result = service.update(id, req, instructorId);

        assertEquals("new", course.title);
        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenUpdatingArchivedCourse() {
        UUID id = UUID.randomUUID();
        Course course = createCourse(instructorId, CourseStatusEnum.ARCHIVED);

        when(repository.findByIdOptional(id)).thenReturn(Optional.of(course));

        CourseRequest req = new CourseRequest("new", "desc", BigDecimal.ONE);

        assertThrows(BadRequestException.class,
                () -> service.update(id, req, instructorId));
    }

    @Test
    void shouldThrowForbiddenWhenUpdatingOtherInstructorCourse() {
        UUID id = UUID.randomUUID();
        Course course = createCourse(otherInstructorId, CourseStatusEnum.DRAFT);

        when(repository.findByIdOptional(id)).thenReturn(Optional.of(course));

        CourseRequest req = new CourseRequest("new", "desc", BigDecimal.ONE);

        assertThrows(ForbiddenException.class,
                () -> service.update(id, req, instructorId));
    }

    // =========================
    // PUBLISH
    // =========================
    @Test
    void shouldPublishCourse() {
        UUID id = UUID.randomUUID();
        Course course = createCourse(instructorId, CourseStatusEnum.DRAFT);

        Module module = new Module();
        module.lessons = List.of(new org.acme.plataforma.courses.models.Lesson());
        course.modules.add(module);

        when(repository.findByIdOptional(id)).thenReturn(Optional.of(course));
        when(mapper.toResponse(course)).thenReturn(mock(CourseResponse.class));

        CourseResponse result = service.publish(id, instructorId);

        assertEquals(CourseStatusEnum.PUBLISHED, course.status);
        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenPublishingWithoutModules() {
        UUID id = UUID.randomUUID();
        Course course = createCourse(instructorId, CourseStatusEnum.DRAFT);

        when(repository.findByIdOptional(id)).thenReturn(Optional.of(course));

        assertThrows(BadRequestException.class,
                () -> service.publish(id, instructorId));
    }

    // =========================
    // ARCHIVE
    // =========================
    @Test
    void shouldArchiveCourse() {
        UUID id = UUID.randomUUID();
        Course course = createCourse(instructorId, CourseStatusEnum.DRAFT);

        when(repository.findByIdOptional(id)).thenReturn(Optional.of(course));
        when(mapper.toResponse(course)).thenReturn(mock(CourseResponse.class));

        CourseResponse result = service.archive(id, instructorId);

        assertEquals(CourseStatusEnum.ARCHIVED, course.status);
        assertNotNull(result);
    }

    // =========================
    // DELETE
    // =========================
    @Test
    void shouldDeleteCourse() {
        UUID id = UUID.randomUUID();
        Course course = createCourse(instructorId, CourseStatusEnum.DRAFT);

        when(repository.findByIdOptional(id)).thenReturn(Optional.of(course));

        service.delete(id, instructorId);

        verify(repository).delete(course);
    }

    @Test
    void shouldThrowWhenDeletingPublishedCourse() {
        UUID id = UUID.randomUUID();
        Course course = createCourse(instructorId, CourseStatusEnum.PUBLISHED);

        when(repository.findByIdOptional(id)).thenReturn(Optional.of(course));

        assertThrows(BadRequestException.class,
                () -> service.delete(id, instructorId));
    }

    // =========================
    // HELPERS
    // =========================
    private Course createCourse(UUID instructorId, CourseStatusEnum status) {
        Course c = new Course();
        c.instructorId = instructorId;
        c.status = status;
        c.modules = new ArrayList<>();
        return c;
    }
}
