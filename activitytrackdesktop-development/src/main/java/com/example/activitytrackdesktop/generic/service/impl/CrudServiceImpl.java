package com.example.activitytrackdesktop.generic.service.impl;

import com.example.activitytrackdesktop.generic.service.inter.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CrudServiceImpl<Entity, Id, Repository extends CrudRepository<Entity, Id>> implements CrudService<Entity, Id> {
    protected Repository repository;

    @Override
    @Transactional
    public void create(Entity entity) {
        repository.save(entity);
    }

    @Override
    @Transactional
    public Entity save(Entity entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public List<Entity> saveAll(List<Entity> entities) {
        Iterable<Entity> iterable = repository.saveAll(entities);
        LinkedList<Entity> list = new LinkedList<>();
        iterable.forEach(list::add);
        return list;
    }

    @Override
    @Transactional
    public boolean existsById(Id id) {
        return repository.existsById(id);
    }

    @Override
    @Transactional
    public List<Entity> readAll() {
        Iterable<Entity> iterable = repository.findAll();
        LinkedList<Entity> list = new LinkedList<>();
        iterable.forEach(list::add);
        return list;
    }

    @Override
    @Transactional
    public List<Entity> readAllById(List<Id> ids) {
        Iterable<Entity> iterable = repository.findAllById(ids);
        LinkedList<Entity> list = new LinkedList<>();
        iterable.forEach(list::add);
        return list;
    }

    @Override
    @Transactional
    public Entity read(Id id) {
        Optional<Entity> o = repository.findById(id);
        if(o.isEmpty()){
            return null;
        }else {
            return o.get();
        }
    }

    @Override
    @Transactional
    public void delete(Entity entity) {
        repository.delete(entity);
    }

    @Override
    @Transactional
    public void deleteById(Id id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAllById(List<Id> ids) {
        repository.deleteAllById(ids);
    }

    @Override
    @Transactional
    public void deleteAll(List<Entity> entities) {
        repository.deleteAll(entities);
    }

    @Override
    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    @Transactional
    public long count() {
        return repository.count();
    }
}
