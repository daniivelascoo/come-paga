package com.example.comepaga.web;

import com.example.comepaga.model.delivery.Factura;
import com.example.comepaga.model.delivery.Pedido;
import com.example.comepaga.model.delivery.Reparto;
import com.example.comepaga.model.user.Repartidor;
import com.example.comepaga.service.PedidoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * The type Pedido rest controller.
 */
@Slf4j
@Controller
@RequestMapping("/come-paga/pedido")
public class PedidoRestController {

    /**
     * The Service.
     */
    private final PedidoService service;

    /**
     * Instantiates a new Pedido rest controller.
     *
     * @param service the service
     */
    @Autowired
    public PedidoRestController(@Qualifier("PedidoService") PedidoService service) {
        this.service = service;
    }

    /**
     * Gets all.
     *
     * @param usuarioId the usuario_id
     * @param request   the request
     * @return the all
     */
    @GetMapping("/{usuario_id}")
    public ResponseEntity<List<Pedido>> getAllByUser(
            @PathVariable("usuario_id") String usuarioId,
            HttpServletRequest request
    ) {
        log.info("Try Get All Pedidos By User, user id: {}", usuarioId);
        return service.getAllPedidosByUser(usuarioId);
    }

    @PutMapping("/{pedido_id}")
    public ResponseEntity<Pedido> update(
            @PathVariable("pedido_id") String pedidoId,
            @RequestBody Pedido pedido,
            HttpServletRequest request
    ) {
        log.info("Try Update for Pedido {}", pedidoId);
        return service.update(pedido, pedidoId, request);
    }

    /**
     * Get response entity.
     *
     * @param pedidoId the pedido id
     * @return the response entity
     */
    @GetMapping("/{pedido_id}/pedido")
    public ResponseEntity<Pedido> get(@PathVariable("pedido_id") String pedidoId) {
        log.info("Try Get Pedido by Id: {}", pedidoId);
        return service.get(pedidoId);
    }

    /**
     * Get response entity.
     *
     * @param request the request
     * @return the response entity
     */
    @GetMapping
    public ResponseEntity<List<Pedido>> getAll(HttpServletRequest request) {
        log.info("Try GetAll Pedidos");
        return service.getAll(request);
    }

    /**
     * Create response entity.
     *
     * @param body      the body
     * @param clienteId the cliente id
     * @param request   the request
     * @return the response entity
     */
    @PostMapping("/{cliente_id}")
    public ResponseEntity<Pedido> create(
            @RequestBody Pedido body,
            @PathVariable("cliente_id") String clienteId,
            HttpServletRequest request) {
        log.info("Try Create Pedido {} for Cliente id {}", body, clienteId);
        body.setUsuarioId(clienteId);
        return service.create(body, request);
    }

    /**
     * Delete response entity.
     *
     * @param pedidoId the usuario id
     * @param request  the request
     * @return the response entity
     */
    @DeleteMapping("/{pedido_id}")
    public ResponseEntity<Void> delete(@PathVariable("pedido_id") String pedidoId, HttpServletRequest request) {
        log.info("Try Delete Pedido {}", pedidoId);
        return service.delete(pedidoId, request);
    }

    /**
     * Accept response entity.
     *
     * @param pedidoId     the pedido id
     * @param repartidorId the repartidor id
     * @param request      the request
     * @return the response entity
     */
    @PutMapping("/{pedido_id}/repartidor")
    public ResponseEntity<Reparto> accept(
            @PathVariable("pedido_id") String pedidoId,
            @RequestBody Repartidor repartidorId, HttpServletRequest request) {
        log.info("Try Accept Pedido {} by {}", pedidoId, repartidorId);
        return service.accept(repartidorId, pedidoId, request);
    }

    /**
     * Gets facturas.
     *
     * @param clienteId the cliente id
     * @param request   the request
     * @return the facturas
     */
    @GetMapping("/{cliente_id}/facturas")
    public ResponseEntity<List<Factura>> getAllFacturas(
            @PathVariable("cliente_id") String clienteId,
            HttpServletRequest request) {
        log.info("Try GetAll Facturas by Cliente {}", clienteId);
        return service.getAllFacturasByUser(clienteId, request);
    }

    /**
     * Gets reparto.
     *
     * @param id      the id
     * @param request the request
     * @return the reparto
     */
    @GetMapping("/{repartidor_id}/reparto")
    public ResponseEntity<Reparto> getReparto(
            @PathVariable("repartidor_id") String id,
            HttpServletRequest request) {
        log.info("Try Get Reparto by Repartidor id: {}", id);
        return service.getReparto(id, request);
    }

}
