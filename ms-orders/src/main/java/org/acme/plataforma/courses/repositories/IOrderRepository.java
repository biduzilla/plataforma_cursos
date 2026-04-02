package org.acme.plataforma.courses.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.acme.plataforma.courses.models.Order;

import java.util.List;
import java.util.UUID;

public interface IOrderRepository extends PanacheRepositoryBase<Order, UUID> {
    boolean existsByUserAndCourse(UUID userId, UUID courseId);

    List<Order> findByUser(UUID userId);

    List<Order> findPendingByCourse(UUID courseId);

    List<Order> findExpired();

}
