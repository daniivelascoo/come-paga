package com.example.comepaga.repo;

import com.example.comepaga.model.delivery.Factura;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaRepository extends MongoRepository<Factura, String> {

    List<Factura> findByClienteIdLike(String clienteId);
}
