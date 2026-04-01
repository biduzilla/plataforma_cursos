package org.acme.plataforma.courses.repositories.impl;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.plataforma.courses.enums.CourseStatusEnum;
import org.acme.plataforma.courses.models.Course;
import org.acme.plataforma.courses.repositories.ICourseRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class CourseRepository implements ICourseRepository {
    @Override
    public PanacheQuery<Course> search(String title, CourseStatusEnum status) {
        Map<String, Object> params = new HashMap<>();
        var query = new StringBuilder("1=1");

        if (title != null && !title.isBlank()) {
            query.append(" and lower(title) like :title");
            params.put("title", "%" + title.toLowerCase() + "%");
        }

        if (status != null) {
            query.append(" and stats = :status");
            params.put("status", status);
        }

        return find(query.toString(), params);
    }

    @Override
    public List<Course> findByInstructor(UUID instructorId) {
        return find("instructorId", instructorId).list();
    }
}
