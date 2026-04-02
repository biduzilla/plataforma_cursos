package org.acme.plataforma.courses.services.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.plataforma.courses.clients.CourseClient;
import org.acme.plataforma.courses.mappers.OrderMapper;
import org.acme.plataforma.courses.producers.OrderEventProducer;
import org.acme.plataforma.courses.repositories.impl.OrderRepository;
import org.acme.plataforma.courses.requests.OrderRequest;
import org.acme.plataforma.courses.responses.CourseResponse;
import org.acme.plataforma.courses.responses.OrderResponse;
import org.acme.plataforma.courses.services.IOrderService;
import org.eclipse.microprofile.rest.client.inject.RestClient;

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
        }catch (Exception e){

        }
        return null;
    }

    @Override
    public OrderResponse cancel(UUID orderId, UUID userId) {
        return null;
    }

    @Override
    public List<OrderResponse> findByUser(UUID userId) {
        return List.of();
    }

    @Override
    public OrderResponse findById(UUID orderId, UUID userId) {
        return null;
    }
}
