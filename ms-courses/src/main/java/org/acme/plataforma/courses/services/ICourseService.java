package org.acme.plataforma.courses.services;

import org.acme.plataforma.courses.enums.CourseStatusEnum;
import org.acme.plataforma.courses.models.Course;
import org.acme.plataforma.courses.requests.CourseRequest;
import org.acme.plataforma.courses.responses.CourseResponse;
import org.acme.plataforma.courses.responses.PageResponse;

import java.util.UUID;

public interface ICourseService {
    PageResponse<CourseResponse> search(
            String title,
            CourseStatusEnum status,
            int page,
            int pageSize);

    Course findById(UUID id);

    CourseResponse create(CourseRequest req, UUID instructorId);

    CourseResponse update(UUID id, CourseRequest req, UUID instructorId);

    CourseResponse publish(UUID id, UUID instructorId);

    CourseResponse archive(UUID id, UUID instructorId);

    void delete(UUID id, UUID instructorId);
}
