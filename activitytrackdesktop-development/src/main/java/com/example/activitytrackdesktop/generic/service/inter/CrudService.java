package com.example.activitytrackdesktop.generic.service.inter;

import java.util.List;
import java.util.Optional;

public interface CrudService<Entity, Id>{
    void create(Entity entity);
    Entity save(Entity entity);
    List<Entity> saveAll(List<Entity> entities);
    boolean existsById(Id id);
    List<Entity> readAll();
    List<Entity> readAllById(List<Id> ids);
    Entity read(Id id);
    void delete(Entity entity);
    void deleteById(Id id);
    void deleteAllById(List<Id> ids);
    void deleteAll(List<Entity> entities);
    void deleteAll();
    long count();
}
