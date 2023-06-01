package com.example.comepaga.service;

import com.example.comepaga.model.delivery.Factura;
import com.example.comepaga.model.delivery.Pedido;
import com.example.comepaga.model.delivery.Reparto;
import com.example.comepaga.model.user.Repartidor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * The interface Pedido service.
 */
@Service
public interface PedidoService extends GenericService<Pedido> {

    /**
     * Accept response entity.
     *
     * @param repartidor the repartidor
     * @param pedidoId   the pedido id
     * @param request    the request
     * @return the response entity
     */
    ResponseEntity<Reparto> accept(Repartidor repartidor, String pedidoId, HttpServletRequest request);

    /**
     * Gets all pedidos by user.
     *
     * @param userId the user id
     * @return the all pedidos by user
     */
    ResponseEntity<List<Pedido>> getAllPedidosByUser(String userId);

    /**
     * Gets all facturas by user.
     *
     * @param userId  the user id
     * @param request the request
     * @return the all facturas by user
     */
    ResponseEntity<List<Factura>> getAllFacturasByUser(String userId, HttpServletRequest request);

    /**
     * Gets factura.
     *
     * @param facturaId the factura id
     * @param request   the request
     * @return the factura
     */
    ResponseEntity<Factura> getFactura(String facturaId, HttpServletRequest request);

    /**
     * Gets all.
     *
     * @param request the request
     * @return the all
     */
    ResponseEntity<List<Pedido>> getAll(HttpServletRequest request);

    /**
     * Gets reparto.
     *
     * @param id      the id
     * @param request the request
     * @return the reparto
     */
    ResponseEntity<Reparto> getReparto(String id, HttpServletRequest request);
}
