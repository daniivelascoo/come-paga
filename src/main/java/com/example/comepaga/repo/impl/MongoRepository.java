package com.example.comepaga.repo.impl;

import com.example.comepaga.repo.CRUDRepository;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The type Mongo repository.
 *
 * @param <T> the type parameter
 */
@Repository("Mongo")
@Slf4j
public class MongoRepository<T> implements CRUDRepository<T> {

    private final MongoTemplate mongo;

    /**
     * Instantiates a new Mongo repository.
     *
     * @param mongo the mongo
     */
    @Autowired
    public MongoRepository(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Override
    public Optional<T> save(T object) {
        log.info("Saving document: {}", object);
        return Optional.of(mongo.save(object));
    }

    @Override
    public Optional<T> findById(@NonNull Object id, @NonNull Class<T> tClass) {
        log.info("FindById object type {} with id {}", tClass, id);
        return Optional.ofNullable(mongo.findById(id, tClass));
    }

    @Override
    public List<T> findAll(Query query, @NonNull Class<T> tClass) {
        if (Objects.isNull(query)) {
            log.info("Find all for document type {}", tClass);
            return mongo.findAll(tClass);
        }

        log.info("Find all with filter {} for document type {}", query, tClass);
        return mongo.find(query, tClass);
    }

    @Override
    public boolean delete(Object id, Class<T> tClass) {
        log.info("Delete by id: {}", id);
        var query = new Query(Criteria.where("_id").is(id));
        DeleteResult result = mongo.remove(query, tClass);
        return result.getDeletedCount() > 0;
    }
}

