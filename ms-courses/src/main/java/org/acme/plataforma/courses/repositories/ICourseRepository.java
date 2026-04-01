package org.acme.plataforma.courses.repositories;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.acme.plataforma.courses.enums.CourseStatusEnum;
import org.acme.plataforma.courses.models.Course;

import java.util.List;
import java.util.UUID;

public interface ICourseRepository extends PanacheRepositoryBase<Course, UUID> {
    public PanacheQuery<Course> search(String title, CourseStatusEnum status);

    public List<Course> findByInstructor(UUID instructorId);

}
