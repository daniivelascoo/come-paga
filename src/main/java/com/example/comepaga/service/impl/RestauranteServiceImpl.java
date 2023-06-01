package com.example.comepaga.service.impl;

import com.example.comepaga.model.restaurant.ImagenRestaurante;
import com.example.comepaga.model.restaurant.Plato;
import com.example.comepaga.model.restaurant.Restaurante;
import com.example.comepaga.model.user.AccessType;
import com.example.comepaga.repo.CRUDRepository;
import com.example.comepaga.repo.query.QueryBuilder;
import com.example.comepaga.service.RestauranteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.MethodNotAllowedException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * The type Restaurante service.
 */
@Service("RestauranteService")
@Slf4j
public class RestauranteServiceImpl implements RestauranteService {

    /**
     * The Restaurante repo.
     */
    private final CRUDRepository<Restaurante> restauranteRepo;
    /**
     * The Grid fs repo.
     */
    private final CRUDRepository<ImagenRestaurante> gridFsRepo;
    /**
     * The Plato repo.
     */
    private final CRUDRepository<Plato> platoRepo;

    /**
     * Instantiates a new Restaurante service.
     *
     * @param restauranteRepo the restaurante repo
     * @param gridFsRepo      the grid fs repo
     * @param platoRepo       the plato repo
     */
    @Autowired
    public RestauranteServiceImpl(
            @Qualifier("Mongo") CRUDRepository<Restaurante> restauranteRepo,
            @Qualifier("GridFs") CRUDRepository<ImagenRestaurante> gridFsRepo,
            @Qualifier("Mongo") CRUDRepository<Plato> platoRepo) {
        this.restauranteRepo = restauranteRepo;
        this.gridFsRepo = gridFsRepo;
        this.platoRepo = platoRepo;
    }

    @Override
    public ResponseEntity<Restaurante> create(Restaurante object, HttpServletRequest request) {
        throw new MethodNotAllowedException("Method not allowed: RestauranteServiceImpl.create()", List.of(HttpMethod.POST));
    }

    @Override
    public ResponseEntity<Restaurante> create(String body, MultipartFile image, HttpServletRequest request) {
        try {
            if (this.detectRestrictedAccess(request, AccessType.ADMINISTRADOR))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);

            ObjectMapper mapper = new ObjectMapper();
            Restaurante r = mapper.readValue(body, Restaurante.class);
            r.calculateHalfPrice();

            Optional<Restaurante> opt = restauranteRepo.save(r);

            if (opt.isPresent()) {
                r = opt.get();
                log.info("The Restaurante was created: {}", r);
                List<String> platosId = new ArrayList<>();

                for (Plato plato : r.getPlatosCrear()) {
                    Optional<Plato> platoOp = platoRepo.save(plato);

                    if (platoOp.isPresent()) {
                        log.info("The Plato {} of the Restaurant {} was created", platoOp.get().getNombre(), r.getNombre());
                        platosId.add(platoOp.get().getId());
                    }
                }

                r.setPlatosCreados(platosId);
                this.update(r, r.getNombre(), request);

                ImagenRestaurante img = new ImagenRestaurante();
                img.setResource(image.getResource());
                img.setContentType(image.getContentType());
                img.setId(r.getNombre());

                Optional<ImagenRestaurante> imgResponse = gridFsRepo.save(img);
                if (imgResponse.isPresent()) {
                    log.info("The ImagenRestaurante was saved: {} {}", img.getId(), img.getFileName());
                    return new ResponseEntity<>(r, HttpStatus.CREATED);
                }
            }

            log.info("The Restaurante was not created: {}", r);
        } catch (Exception e) {
            log.warn("Exception occurred during de map of the Restaurante: {}", body);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Restaurante> update(Restaurante object, String id, HttpServletRequest request) {
        try {
            if (detectRestrictedAccess(request, AccessType.ADMINISTRADOR))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            log.info("Access accepted for Update");
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        object.setNombre(id);
        Optional<Restaurante> opt = restauranteRepo.save(object);

        if (opt.isPresent()) {
            log.info("The Restaurante was Updated {}", opt.get());
            return new ResponseEntity<>(opt.get(), HttpStatus.OK);
        }

        log.info("The Restaurante was not Updated {}", id);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Restaurante> get(String id) {
        Optional<Restaurante> opt = restauranteRepo.findById(id, Restaurante.class);

        if (opt.isPresent()) {
            log.info("Te Restaurante was found: {}", opt.get());
            return new ResponseEntity<>(opt.get(), HttpStatus.OK);
        }

        log.warn("The Restaurante was not found: {}", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Void> delete(String id, HttpServletRequest request) {
        try {
            if (detectRestrictedAccess(request, AccessType.ADMINISTRADOR))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (restauranteRepo.delete(id, Restaurante.class)) {
            gridFsRepo.delete(id, ImagenRestaurante.class);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<List<Restaurante>> getAll(Map<String, Object> filter) {
        var query = new QueryBuilder(filter);
        List<Restaurante> result = restauranteRepo.findAll(query.build(), Restaurante.class);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Resource> getImage(String id) {
        Optional<ImagenRestaurante> opt = gridFsRepo.findById(id, ImagenRestaurante.class);

        if (opt.isPresent() && Objects.nonNull(opt.get().getResource())) {
            ImagenRestaurante r = opt.get();
            log.info("The ImagenRestaurante was Found: {}", r.getFileName());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.parseMediaType(r.getContentType()))
                    .body(r.getResource());
        }

        log.info("The ImagenRestaurante was not Found: {}", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Restaurante> update(String body, String id, MultipartFile file, HttpServletRequest request) {
        try {
            if (detectRestrictedAccess(request, AccessType.ADMINISTRADOR))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            Restaurante r = mapper.readValue(body, Restaurante.class);
            r.setNombre(id);

            if (! restauranteRepo.exists(r.getNombre(), Restaurante.class)) {
                log.warn("The Restaurant doesn't existes.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            for (Plato plato: r.getPlatosCrear()) {
                if (! platoRepo.exists(plato.getId(), Plato.class)) {
                    log.warn("The Plato doesn't exists.");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                if (r.getPlatosCreados().contains(plato.getId())) {
                    platoRepo.save(plato);
                }
            }

            Optional<Restaurante> restaurante = restauranteRepo.save(r);
            if (restaurante.isPresent()) {
                log.info("The restaurant has been Updated");

                gridFsRepo.delete(id, ImagenRestaurante.class);
                ImagenRestaurante img = new ImagenRestaurante();
                img.setResource(file.getResource());
                img.setContentType(file.getContentType());
                img.setId(r.getNombre());
                gridFsRepo.save(img);

                return new ResponseEntity<>(restaurante.get(), HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
