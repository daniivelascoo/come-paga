package com.example.comepaga.web;

import com.example.comepaga.model.user.Administrador;
import com.example.comepaga.model.user.Cliente;
import com.example.comepaga.model.user.Repartidor;
import com.example.comepaga.model.user.Usuario;
import com.example.comepaga.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * The type Usuario rest controller.
 */
@Controller
@RequestMapping("/come-paga/usuario")
@Slf4j
public class UsuarioRestController {

    /**
     * The Service.
     */
    private final UsuarioService service;

    /**
     * Instantiates a new Usuario rest controller.
     *
     * @param usuarioService the usuario service
     */
    @Autowired
    public UsuarioRestController(@Qualifier("UsuarioService") UsuarioService usuarioService) {
        this.service = usuarioService;
    }

    /**
     * Login response entity.
     *
     * @param usuarioId the usuario id
     * @param password  the password
     * @param response  the response
     * @param request   the request
     * @return the response entity
     */
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(
            @RequestPart(value = "id", required = false) String usuarioId,
            @RequestPart(value = "password", required = false) String password,
            HttpServletResponse response,
            HttpServletRequest request) {
        log.info("Try login: {} {}", usuarioId, password);

        return service.login(password, usuarioId, response, request);
    }

    /**
     * Get response entity.
     *
     * @param usuarioId the usuario id
     * @return the response entity
     */
    @GetMapping("/{usuario_id}")
    public ResponseEntity<Usuario> get(@PathVariable("usuario_id") String usuarioId) {
        log.info("Try Get Usuario by id: {}", usuarioId);
        return service.get(usuarioId);
    }

    /**
     * Gets all.
     *
     * @param filter the filter
     * @return the all
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> getAll(@RequestParam Map<String, Object> filter) {
        log.info("Try Get All with filter: {}", filter);
        return service.getAll(filter);
    }

    /**
     * Create cliente response entity.
     *
     * @param body    the body
     * @param request the request
     * @return the response entity
     */
    @PostMapping("/cliente")
    public ResponseEntity<Usuario> create(@RequestBody Cliente body, HttpServletRequest request) {
        log.info("Create cliente: {}", body);
        return service.create(body, request);
    }

    /**
     * Create repartidor response entity.
     *
     * @param body the body
     * @return the response entity
     */
    @PostMapping("/repartidor")
    public ResponseEntity<Usuario> createRepartidor(@RequestBody Repartidor body) {
        log.info("Try Create Repartidor: {}", body);
        return service.createRepartidor(body);
    }

    /**
     * Create administrador response entity.
     *
     * @param body the body
     * @return the response entity
     */
    @PostMapping("/administrador")
    public ResponseEntity<Usuario> createAdministrador(@RequestBody Administrador body) {
        log.info("Try Create Administrador: {}", body);
        return service.createAdmin(body);
    }

    /**
     * Update cliente response entity.
     *
     * @param body      the body
     * @param usuarioId the usuario id
     * @return the response entity
     */
    @PutMapping("/{usuario_id}/cliente")
    public ResponseEntity<Usuario> updateCliente(
            @RequestBody Cliente body,
            @PathVariable("usuario_id") String usuarioId,
            HttpServletRequest request) {
        log.info("Update cliente {}: {}", usuarioId, body);
        return service.update(body, usuarioId, request);
    }

    /**
     * Update repartidor response entity.
     *
     * @param body      the body
     * @param usuarioId the usuario id
     * @param request   the request
     * @return the response entity
     */
    @PutMapping("/{usuario_id}/repartidor")
    public ResponseEntity<Usuario> updateRepartidor(
            @RequestBody Repartidor body,
            @PathVariable("usuario_id") String usuarioId,
            HttpServletRequest request) {
        log.info("Try Update repartidor {}: {}", usuarioId, body);
        return service.update(body, usuarioId, request);

    }

    /**
     * Update admin response entity.
     *
     * @param body      the body
     * @param usuarioId the usuario id
     * @param request   the request
     * @return the response entity
     */
    @PutMapping("/{usuario_id}/admin")
    public ResponseEntity<Usuario> updateAdmin(
            @RequestBody Administrador body,
            @PathVariable("usuario_id") String usuarioId,
            HttpServletRequest request) {
        log.info("Update admin {}: {}", usuarioId, body);
        return service.update(body, usuarioId, request);
    }


}
