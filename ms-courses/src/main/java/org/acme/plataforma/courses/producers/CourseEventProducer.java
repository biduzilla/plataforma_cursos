package org.acme.plataforma.courses.producers;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.plataforma.courses.events.CourseArchivedEvent;
import org.acme.plataforma.courses.models.Course;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.time.LocalDateTime;

@ApplicationScoped
public class CourseEventProducer {
    @Channel("course-archived")
    Emitter<CourseArchivedEvent> courseArchivedEventEmitter;

    public void publishCourseArchived(Course course) {
        var event = new CourseArchivedEvent(
                course.id,
                course.title,
                LocalDateTime.now()
        );
        courseArchivedEventEmitter.send(event);
    }
}
