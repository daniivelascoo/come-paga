package com.example.comepaga.web;

import com.example.comepaga.model.restaurant.Restaurante;
import com.example.comepaga.service.RestauranteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * The type Restaurante rest controller.
 */
@Slf4j
@Controller
@RequestMapping("/come-paga/restaurante")
public class RestauranteRestController {

    /**
     * The Service.
     */
    private final RestauranteService service;

    /**
     * Instantiates a new Restaurante rest controller.
     *
     * @param service the service
     */
    @Autowired
    public RestauranteRestController(@Qualifier("RestauranteService") RestauranteService service) {
        this.service = service;
    }

    /**
     * Create response entity.
     *
     * @param restaurante the restaurante
     * @param image       the image
     * @param request     the request
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<Restaurante> create(
            @RequestPart("body") String restaurante,
            @RequestPart("image") MultipartFile image,
            HttpServletRequest request){
        log.info("Try Create Restaurante: {}", restaurante);
        return service.create(restaurante, image, request);
    }

    /**
     * Update response entity.
     *
     * @param body          the body
     * @param restauranteId the restaurante id
     * @param request       the request
     * @return the response entity
     */
    @PostMapping("/{restaurante_id}")
    public ResponseEntity<Restaurante> update(
            @RequestPart("image") MultipartFile file,
            @RequestPart("body") String body,
            @PathVariable("restaurante_id") String restauranteId,
            HttpServletRequest request) {
        log.info("Try Update Restaurante {}, id {}", body, restauranteId);
        return service.update(body, restauranteId, file, request);
    }

    /**
     * Get response entity.
     *
     * @param restauranteId the restaurante id
     * @return the response entity
     */
    @GetMapping("/{restaurante_id}")
    public ResponseEntity<Restaurante> get(@PathVariable("restaurante_id") String restauranteId) {
        log.info("Try Get Restaurante id: {}", restauranteId);
        return service.get(restauranteId);
    }

    /**
     * Gets image.
     *
     * @param restauranteId the restaurante id
     * @return the image
     */
    @GetMapping("/{restaurante_id}/img")
    public ResponseEntity<Resource> getImage(@PathVariable("restaurante_id") String restauranteId) {
        log.info("Try Get Image of the Restaurant {}", restauranteId);
        return service.getImage(restauranteId);
    }

    /**
     * Gets all.
     *
     * @param filter the filter
     * @return the all
     */
    @GetMapping
    public ResponseEntity<List<Restaurante>> getAll(@RequestParam Map<String, Object> filter) {
        log.info("Try Get All Restaurante. Filter: {}", filter);
        return service.getAll(filter);
    }

    /**
     * Delete response entity.
     *
     * @param restauranteId the restaurante id
     * @param request       the request
     * @return the response entity
     */
    @DeleteMapping("{restaurante_id}")
    public ResponseEntity<Void> delete(@PathVariable("restaurante_id") String restauranteId, HttpServletRequest request) {
        log.info("Try Delete By Id: {}", restauranteId);
        return service.delete(restauranteId, request);
    }
}
