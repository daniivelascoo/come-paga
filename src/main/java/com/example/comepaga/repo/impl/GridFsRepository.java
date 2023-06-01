package com.example.comepaga.repo.impl;

import com.example.comepaga.model.gridfs.GridFsObject;
import com.example.comepaga.repo.CRUDRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * The type Grid fs repository.
 *
 * @param <T> the type parameter
 */
@Slf4j
@Repository("GridFs")
public class GridFsRepository<T extends GridFsObject> implements CRUDRepository<T> {

    private final GridFsTemplate gridFsTemplate;

    /**
     * Instantiates a new Grid fs repository.
     *
     * @param gridFsTemplate the grid fs template
     */
    @Autowired
    public GridFsRepository(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }


    @Override
    public Optional<T> save(T o) {
        DBObject metadata = new BasicDBObject();
        for (Field f: o.getClass().getDeclaredFields()) {
            try {
                f.setAccessible(true);
                if (f.get(o) != null) {
                    metadata.put(f.getAnnotation(JsonProperty.class).value(), f.get(o));
                }
            } catch (Exception e) {
                log.error("Error creating the Metadata: {}", e.getMessage());
                return Optional.empty();
            }
        }
        metadata.removeField("imagen");

        try {
            gridFsTemplate.store(o.getInputStream(), o.getFileName(), o.getContentType(), metadata);

            return this.findById(o.getId(), (Class<T>) o.getClass());
        } catch (IOException e) {
            log.error("Error when Store the GridFs Document: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<T> findById(Object id, Class<T> tClass) {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("metadata._id").is(id)));

        try {
            if (Objects.nonNull(file) && Objects.nonNull(file.getMetadata())) {
                T object = mapFileToClass(tClass, file);
                return Optional.of(object);
            }
        } catch (Exception e) {
            log.error("Error when map the data of the File {} to te Object type: {}. Error: {}", id, tClass, e.getMessage());
        }

        return Optional.empty();
    }

    private T mapFileToClass(@NonNull Class<T> tClass, @NonNull GridFSFile file) throws Exception {
        Objects.requireNonNull(file.getMetadata(), "The metadata is null");

        Optional<Constructor<?>> c = Arrays.stream(tClass.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0).findFirst();
        if (c.isEmpty()) throw new Exception("The class didn't have a defaults constructor");

        T object = (T) c.get().newInstance();
        for (Field f: tClass.getDeclaredFields()) {
            f.setAccessible(true);
            f.set(object, file.getMetadata().get(f.getAnnotation(JsonProperty.class).value()));
        }

        object.setResource(gridFsTemplate.getResource(file));
        return object;
    }

    @Override
    public List<T> findAll(Query query, Class<T> restauranteClass) {
        List<T> listType = new ArrayList<>();

        gridFsTemplate.find(new Query()).forEach(file -> {
            try {
                listType.add(mapFileToClass(restauranteClass, file));
            } catch (Exception e) {
                log.error("Error when map the data of the File {} to te Object type: {}. Error: {}", file.getId(), restauranteClass, e.getMessage());
            }
        });

        return listType;
    }

    @Override
    public boolean delete(Object id, Class<T> tClass) {
        gridFsTemplate.delete(new Query(Criteria.where("metadata._id").is(id)));
        return this.findById(id, tClass).isEmpty();
    }
}
