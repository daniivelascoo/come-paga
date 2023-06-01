package com.example.comepaga.repo;

import com.example.comepaga.model.restaurant.Restaurante;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The interface Restaurante repository.
 */
@Repository
public interface RestauranteRepository extends MongoRepository<Restaurante, String> {

    /**
     * Find all by categoria like list.
     *
     * @param categoria the categoria
     * @return the list
     */
    List<Restaurante> findAllByCategoriaLike(String categoria);

    /**
     * Find all by nombre like list.
     *
     * @param nombre the nombre
     * @return the list
     */
    List<Restaurante> findAllByNombreLike(String nombre);
}
