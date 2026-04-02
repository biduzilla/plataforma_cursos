package org.acme.plataforma.courses.repositories.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.plataforma.courses.enums.OrderStatus;
import org.acme.plataforma.courses.models.Order;
import org.acme.plataforma.courses.repositories.IOrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OrderRepository implements IOrderRepository {
    @Override
    public boolean existsByUserAndCourse(UUID userId, UUID courseId) {
        return count("userId = ?1 and courseId = ?2 and status = ?3",
                userId, courseId, OrderStatus.PAID) > 0;
    }

    @Override
    public List<Order> findByUser(UUID userId) {
        return find("userId = ?1 order by createdAt desc",
                userId).list();
    }

    @Override
    public List<Order> findPendingByCourse(UUID courseId) {
        return find("courseId = ?1 and status = ?2",
                courseId, OrderStatus.PENDING).list();
    }

    @Override
    public List<Order> findExpired() {
        return find("status = ?1 and expiresAt < ?2",
                OrderStatus.PENDING, LocalDateTime.now()).list();
    }
}
