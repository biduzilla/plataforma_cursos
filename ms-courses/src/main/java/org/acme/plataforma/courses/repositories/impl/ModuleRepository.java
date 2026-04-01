package org.acme.plataforma.courses.repositories.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.plataforma.courses.models.Module;
import org.acme.plataforma.courses.repositories.IModuleRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ModuleRepository implements IModuleRepository {
    @Override
    public List<Module> findByCourse(UUID courseId) {
        return find("course.id", courseId).list();
    }
}
