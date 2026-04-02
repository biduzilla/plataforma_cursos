package org.acme.plataforma.courses.services;

import org.acme.plataforma.courses.requests.OrderRequest;
import org.acme.plataforma.courses.responses.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface IOrderService {
    OrderResponse create(OrderRequest req, UUID userId);

    OrderResponse cancel(UUID orderId, UUID userId);

    List<OrderResponse> findByUser(UUID userId);

    OrderResponse findById(UUID orderId, UUID userId);
}
