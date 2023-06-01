package com.example.comepaga.service;

import com.example.comepaga.model.user.Administrador;
import com.example.comepaga.model.user.Repartidor;
import com.example.comepaga.model.user.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * The interface Usuario service.
 */
@Service
public interface UsuarioService extends GenericService<Usuario> {

    /**
     * Login response entity.
     *
     * @param password the password
     * @param name     the name
     * @param response the response
     * @param request  the request
     * @return the response entity
     */
    ResponseEntity<Usuario> login(String password, String name, HttpServletResponse response, HttpServletRequest request);

    /**
     * Gets all.
     *
     * @param filter the filter
     * @return the all
     */
    ResponseEntity<List<Usuario>> getAll(Map<String, Object> filter);

    /**
     * Create repartidor response entity.
     *
     * @param repartidor the repartidor
     * @return the response entity
     */
    ResponseEntity<Usuario> createRepartidor(Repartidor repartidor);

    /**
     * Create admin response entity.
     *
     * @param body the body
     * @return the response entity
     */
    ResponseEntity<Usuario> createAdmin(Administrador body);
}
