package org.acme.plataforma.courses.services.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.plataforma.courses.clients.CourseClient;
import org.acme.plataforma.courses.enums.OrderStatus;
import org.acme.plataforma.courses.exceptions.CourseNotAvailableException;
import org.acme.plataforma.courses.exceptions.ForbiddenException;
import org.acme.plataforma.courses.exceptions.OrderCancellationException;
import org.acme.plataforma.courses.exceptions.OrderNotFoundException;
import org.acme.plataforma.courses.mappers.OrderMapper;
import org.acme.plataforma.courses.models.Order;
import org.acme.plataforma.courses.producers.OrderEventProducer;
import org.acme.plataforma.courses.repositories.impl.OrderRepository;
import org.acme.plataforma.courses.requests.OrderRequest;
import org.acme.plataforma.courses.responses.CourseResponse;
import org.acme.plataforma.courses.responses.OrderResponse;
import org.acme.plataforma.courses.services.IOrderService;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OrderService implements IOrderService {
    @Inject
    OrderRepository orderRepository;
    @Inject
    OrderEventProducer eventProducer;
    @Inject
    OrderMapper mapper;

    @RestClient
    CourseClient courseClient;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest req, UUID userId) {
        CourseResponse course;

        try {
            course = courseClient.findById(req.courseId());
        } catch (Exception e) {
            throw new CourseNotAvailableException("Curso não encontrado ou indisponível");
        }

        if (!"PUBLISHED".equals(course.status())) {
            throw new CourseNotAvailableException("Curso não está disponível para compra");
        }

        if (orderRepository.existsByUserAndCourse(userId, req.courseId())) {
            throw new OrderCancellationException("Você já adquiriu este curso");
        }

        Order order = new Order();
        order.userId = userId;
        order.courseId = course.id();
        order.courseTitle = course.title();
        order.amount = course.price();
        order.status = OrderStatus.PENDING;

        orderRepository.persist(order);

        eventProducer.publishOrderCreated(order);

        return mapper.toResponse(order);
    }

    @Override
    public OrderResponse cancel(UUID orderId, UUID userId) {
        Order order = orderRepository.findByIdOptional(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.userId.equals(userId)) {
            throw new ForbiddenException("Sem permissão para cancelar este pedido");
        }

        if (order.status != OrderStatus.PENDING) {
            throw new OrderCancellationException(
                    "Apenas pedidos pendentes podem ser cancelados"
            );
        }

        order.status = OrderStatus.CANCELLED;
        order.updatedAt = LocalDateTime.now();

        return mapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> findByUser(UUID userId) {
        return orderRepository.findByUser(userId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public OrderResponse findById(UUID orderId, UUID userId) {
        Order order = orderRepository.findByIdOptional(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.userId.equals(userId)) {
            throw new ForbiddenException("Sem permissão");
        }

        return mapper.toResponse(order);
    }
}
