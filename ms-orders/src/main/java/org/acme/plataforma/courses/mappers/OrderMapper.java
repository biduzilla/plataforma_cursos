package org.acme.plataforma.courses.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.plataforma.courses.models.Order;
import org.acme.plataforma.courses.responses.OrderResponse;

@ApplicationScoped
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.id,
                order.userId,
                order.courseId,
                order.courseTitle,
                order.amount,
                order.status,
                order.createdAt,
                order.expiresAt
        );
    }
}
