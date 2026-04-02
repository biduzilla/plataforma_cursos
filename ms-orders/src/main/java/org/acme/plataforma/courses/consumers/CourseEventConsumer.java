package org.acme.plataforma.courses.consumers;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.plataforma.courses.enums.OrderStatus;
import org.acme.plataforma.courses.events.CourseArchivedEvent;
import org.acme.plataforma.courses.models.Order;
import org.acme.plataforma.courses.repositories.IOrderRepository;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class CourseEventConsumer {
    @Inject
    IOrderRepository orderRepository;

    @Incoming("course-archived")
    @Transactional
    public void onCourseArchived(CourseArchivedEvent event) {
        Log.infof("Curso arquivado recebido: %s", event.courseId());

        List<Order> pendingOrders = orderRepository
                .findPendingByCourse(event.courseId());

        if(pendingOrders.isEmpty()){
            Log.infof("Nenhum pedido pendente para o curso: %s", event.courseId());
            return;
        }

        pendingOrders.forEach(order->{
            order.status = OrderStatus.CANCELLED;
            order.updatedAt = LocalDateTime.now();
        });
        Log.infof("%d pedidos cancelados para o curso: %s",
                pendingOrders.size(), event.courseId());
    }
}
