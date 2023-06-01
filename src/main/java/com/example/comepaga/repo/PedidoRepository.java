package com.example.comepaga.repo;

import com.example.comepaga.model.delivery.Pedido;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The interface Pedido repository.
 */
@Repository
public interface PedidoRepository extends MongoRepository<Pedido, String> {

    /**
     * Find by usuario id like list.
     *
     * @param usuarioId the usuario id
     * @return the list
     */
    List<Pedido> findByUsuarioIdLike(String usuarioId);
}
