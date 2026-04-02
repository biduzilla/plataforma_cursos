    package org.acme.plataforma.courses.producers;

    import io.quarkus.logging.Log;
    import jakarta.enterprise.context.ApplicationScoped;
    import org.acme.plataforma.courses.events.OrderCreatedEvent;
    import org.acme.plataforma.courses.models.Order;
    import org.eclipse.microprofile.reactive.messaging.Channel;
    import org.eclipse.microprofile.reactive.messaging.Emitter;

    @ApplicationScoped
    public class OrderEventProducer {

        @Channel("order-created")
        Emitter<OrderCreatedEvent> orderCreatedEmitter;

        public void publishOrderCreated(Order order) {
            var event = new OrderCreatedEvent(
                    order.id,
                    order.userId,
                    order.courseId,
                    order.courseTitle,
                    order.amount,
                    order.createdAt
            );

            orderCreatedEmitter.send(event)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            Log.errorf("Erro ao publicar order.created: %s", error.getMessage());
                        }
                    });
        }
    }
