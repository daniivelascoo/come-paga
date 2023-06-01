package com.example.comepaga.service;

import com.example.comepaga.model.restaurant.Restaurante;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * The interface Restaurante service.
 */
@Service
public interface RestauranteService extends GenericService<Restaurante> {
    /**
     * Create response entity.
     *
     * @param body    the body
     * @param image   the image
     * @param request the request
     * @return the response entity
     */
    ResponseEntity<Restaurante> create(String body, MultipartFile image, HttpServletRequest request);


    /**
     * Gets all.
     *
     * @param filter the filter
     * @return the all
     */
    ResponseEntity<List<Restaurante>> getAll(Map<String, Object> filter);

    /**
     * Gets image.
     *
     * @param id the id
     * @return the image
     */
    ResponseEntity<Resource> getImage(String id);

    /**
     * Update response entity.
     *
     * @param body    the body
     * @param id      the id
     * @param file    the file
     * @param request the request
     * @return the response entity
     */
    ResponseEntity<Restaurante> update(String body, String id, MultipartFile file, HttpServletRequest request);
}
