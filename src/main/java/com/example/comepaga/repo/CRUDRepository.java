package com.example.comepaga.repo;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The interface Crud repository.
 *
 * @param <T> the type parameter
 */
@Repository
public interface CRUDRepository<T> {

    /**
     * Create optional.
     *
     * @param object the object
     * @return the optional
     */
    Optional<T> save(T object);

    /**
     * Find by id optional.
     *
     * @param id     the id
     * @param tClass the t class
     * @return the optional
     */
    Optional<T> findById(Object id, Class<T> tClass);

    /**
     * Find all list.
     *
     * @param query  the query
     * @param tClass the t class
     * @return the list
     */
    List<T> findAll(Query query, Class<T> tClass);

    /**
     * Delete optional.
     *
     * @param id     the id
     * @param tClass te class type
     * @return the optional
     */
    boolean delete(Object id, Class<T> tClass);

    /**
     * Exists boolean.
     *
     * @param id     the id
     * @param tClass the t class
     * @return the boolean
     */
    default boolean exists(Object id, Class<T> tClass){
        return findById(id, tClass).isPresent();
    }
}
