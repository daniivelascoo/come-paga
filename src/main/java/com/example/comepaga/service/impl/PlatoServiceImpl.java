package com.example.comepaga.service.impl;

import com.example.comepaga.model.restaurant.Plato;
import com.example.comepaga.repo.CRUDRepository;
import com.example.comepaga.service.PlatoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.MethodNotAllowedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * The type Plato service.
 */
@Service
@Slf4j
public class PlatoServiceImpl implements PlatoService {

    /**
     * The Plato repo.
     */
    private final CRUDRepository<Plato> platoRepo;

    /**
     * Instantiates a new Plato service.
     *
     * @param platoRepo the plato repo
     */
    public PlatoServiceImpl(@Qualifier("Mongo") CRUDRepository<Plato> platoRepo) {
        this.platoRepo = platoRepo;
    }

    @Override
    public ResponseEntity<Plato> create(Plato object, HttpServletRequest request) {
        throw new MethodNotAllowedException("Method not allowed: PlatoServiceImpl.create()", List.of(HttpMethod.POST));
    }

    @Override
    public ResponseEntity<Plato> update(Plato object, String id, HttpServletRequest request) {
        throw new MethodNotAllowedException("Method not allowed: PlatoServiceImpl.update()", List.of(HttpMethod.PUT));
    }

    @Override
    public ResponseEntity<Plato> get(String id) {
        Optional<Plato> op = this.platoRepo.findById(id, Plato.class);

        if (op.isPresent()) {
            log.info("The Plato has been found {}", op.get());
            return new ResponseEntity<>(op.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Void> delete(String id, HttpServletRequest request) {
        throw new MethodNotAllowedException("Method not allowed: PlatoServiceImpl.delete()", List.of(HttpMethod.DELETE));
    }
}
