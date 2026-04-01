package org.acme.plataforma.courses.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.acme.plataforma.courses.models.Module;

import java.util.List;
import java.util.UUID;

public interface IModuleRepository extends PanacheRepositoryBase<Module, UUID> {
    List<Module> findByCourse(UUID courseId);
}
