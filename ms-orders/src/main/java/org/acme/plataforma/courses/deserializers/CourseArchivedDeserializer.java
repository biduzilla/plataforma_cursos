package org.acme.plataforma.courses.deserializers;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.acme.plataforma.courses.events.CourseArchivedEvent;

public class CourseArchivedDeserializer
        extends ObjectMapperDeserializer<CourseArchivedEvent> {
    public CourseArchivedDeserializer() {
        super(CourseArchivedEvent.class);
    }
}
