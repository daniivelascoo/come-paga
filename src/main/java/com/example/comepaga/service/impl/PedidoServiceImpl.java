package com.example.comepaga.service.impl;

import com.example.comepaga.model.delivery.Factura;
import com.example.comepaga.model.delivery.Pedido;
import com.example.comepaga.model.delivery.PlatoFactura;
import com.example.comepaga.model.delivery.Reparto;
import com.example.comepaga.model.restaurant.Plato;
import com.example.comepaga.model.user.AccessType;
import com.example.comepaga.model.user.Cliente;
import com.example.comepaga.model.user.Repartidor;
import com.example.comepaga.model.user.Usuario;
import com.example.comepaga.repo.CRUDRepository;
import com.example.comepaga.repo.FacturaRepository;
import com.example.comepaga.repo.PedidoRepository;
import com.example.comepaga.repo.query.QueryBuilder;
import com.example.comepaga.service.PedidoService;
import com.example.comepaga.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Pedido service.
 */
@Service("PedidoService")
@Slf4j
public class PedidoServiceImpl implements PedidoService {

    /**
     * The Pedido crud repo.
     */
    private final CRUDRepository<Pedido> pedidoCRUDRepo;
    /**
     * The Usuario crud repo.
     */
    private final CRUDRepository<Cliente> usuarioCRUDRepo;
    /**
     * The Reparto crud repo.
     */
    private final CRUDRepository<Reparto> repartoCRUDRepo;
    /**
     * The Plato crud repo.
     */
    private final CRUDRepository<Plato> platoCRUDRepo;
    /**
     * The Factura repo.
     */
    private final FacturaRepository facturaRepo;
    /**
     * The Pedido repository.
     */
    private final PedidoRepository pedidoRepository;
    /**
     * The Repartidor crud repo.
     */
    private final CRUDRepository<Repartidor> repartidorCRUDRepo;

    /**
     * Instantiates a new Pedido service.
     *
     * @param pedidoCRUDRepo     the pedido crud repo
     * @param usuarioCRUDRepo    the usuario crud repo
     * @param repartoCRUDRepo    the reparto crud repo
     * @param platoCRUDRepo      the plato crud repo
     * @param pedidoRepository   the pedido repository
     * @param facturaRepo        the factura repo
     * @param repartidorCRUDRepo the repartidor crud repo
     */
    @Autowired
    public PedidoServiceImpl(@Qualifier("Mongo") CRUDRepository<Pedido> pedidoCRUDRepo,
                             @Qualifier("Mongo") CRUDRepository<Cliente> usuarioCRUDRepo,
                             @Qualifier("Mongo") CRUDRepository<Reparto> repartoCRUDRepo,
                             @Qualifier("Mongo") CRUDRepository<Plato> platoCRUDRepo,
                             PedidoRepository pedidoRepository,
                             FacturaRepository facturaRepo,
                             @Qualifier("Mongo") CRUDRepository<Repartidor> repartidorCRUDRepo) {
        this.pedidoCRUDRepo = pedidoCRUDRepo;
        this.usuarioCRUDRepo = usuarioCRUDRepo;
        this.repartoCRUDRepo = repartoCRUDRepo;
        this.pedidoRepository = pedidoRepository;
        this.platoCRUDRepo = platoCRUDRepo;
        this.facturaRepo = facturaRepo;
        this.repartidorCRUDRepo = repartidorCRUDRepo;
    }

    @Override
    public ResponseEntity<Pedido> create(Pedido object, HttpServletRequest request) {
        try {
            if (detectRestrictedAccess(request, AccessType.CLIENTE)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        object.setCodigoEstadoPedido("0.33");
        Optional<Pedido> opt = pedidoCRUDRepo.save(object);

        if (opt.isPresent()) {
            log.info("Pedido created: {}", object);
            createFactura(opt.get());
            return new ResponseEntity<>(opt.get(), HttpStatus.CREATED);
        }

        log.warn("Pedido not created: {}", object);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Pedido> update(Pedido object, String id, HttpServletRequest request) {

        try {
            if (detectRestrictedAccess(request, AccessType.REPARTIDOR)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        object.setId(id);
        Optional<Pedido> pedidoOpt = pedidoCRUDRepo.save(object);

        return pedidoOpt.map(pedido -> {
            log.info("The Pedido was updated: {}", pedido);
            return new ResponseEntity<>(pedido, HttpStatus.OK);
        }).orElseGet(() -> {
            log.warn("The Pedido was not found: {}", object);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        });
    }

    @Override
    public ResponseEntity<Pedido> get(String id) {
        Optional<Pedido> pedidoOpt = pedidoCRUDRepo.findById(id, Pedido.class);

        if (pedidoOpt.isPresent()) {
            log.info("The Pedido was found: {}", pedidoOpt.get());
            return new ResponseEntity<>(pedidoOpt.get(), HttpStatus.OK);
        }

        log.warn("The Pedido was not found: {}", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Void> delete(String id, HttpServletRequest request) {
        if (pedidoCRUDRepo.delete(id, Pedido.class)) {
            log.info("The Pedido was deleted: {}", id);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        log.warn("Pedido not found, id: {}", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Synchronized
    @Override
    public ResponseEntity<Reparto> accept(Repartidor repartidor, String pedidoId, HttpServletRequest request) {
        try {
            if (detectRestrictedAccess(request, AccessType.REPARTIDOR))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Pedido> pedidoOpt = pedidoCRUDRepo.findById(pedidoId, Pedido.class);

        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();

            if (Objects.nonNull(pedido.getRepartidorId())) {
                log.warn("The order {} is already being served by another delivery person", pedidoId);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            Optional<Cliente> usuarioOpt = usuarioCRUDRepo.findById(pedido.getUsuarioId(), Cliente.class);
            repartidor.setPedidoId(pedidoId);

            if (usuarioOpt.isPresent()) {
                repartidorCRUDRepo.save(repartidor);
                log.info("The Repartidos has been Updated");

                pedido.setRepartidorId(repartidor.getNombreUsuario());
                pedidoCRUDRepo.save(pedido);

                Reparto reparto = Reparto.create(pedido);
                repartoCRUDRepo.save(reparto);
                log.info("The Reparto has been created correctly");

                Cliente cliente = usuarioOpt.get();
                cliente.addNewPedido(pedido);
                usuarioCRUDRepo.save(cliente);
                log.info("The Cliente has been Updated");

                log.info("The Pedido was accepted: {}", reparto);
                return new ResponseEntity<>(reparto, HttpStatus.OK);
            }
        }

        log.warn("The Pedido was not accepted: {}", pedidoId);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<List<Pedido>> getAllPedidosByUser(String userId) {
        return new ResponseEntity<>(pedidoRepository.findByUsuarioIdLike(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Factura>> getAllFacturasByUser(String userId, HttpServletRequest request) {
        try {
            Optional<Usuario> op = getUserCookieValue(request);

            if (!(op.isPresent() && op.get().getNombreUsuario().equals(userId))) {
                log.info("Se ha detectado que un Usuario quería obtener las Facturas de otro Usuario. FORBIDDEN");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            if (detectRestrictedAccess(request, AccessType.CLIENTE)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(facturaRepo.findByClienteIdLike(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Factura> getFactura(String facturaId, HttpServletRequest request) {

        Optional<Factura> factura = facturaRepo.findById(facturaId);

        if (factura.isPresent()) {
            try {
                Optional<Usuario> op = getUserCookieValue(request);

                if (!(op.isPresent() && factura.get().getClienteId().equals(op.get().getNombreUsuario()))) {
                    log.info("Un Usuario ha intentado obtener una factura que no le pertenece.");
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }

                if (detectRestrictedAccess(request, AccessType.CLIENTE))
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);

            } catch (JsonProcessingException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            log.info("The Factura {} has been found.", facturaId);
            return new ResponseEntity<>(factura.get(), HttpStatus.OK);
        }

        log.info("The Factura {} hasn't been found.", facturaId);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<List<Pedido>> getAll(HttpServletRequest request) {

        try {
            if (detectRestrictedAccess(request, AccessType.REPARTIDOR))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> filter = new HashMap<>();
        filter.put("repartidorId", null);
        var query = new QueryBuilder(filter);

        return new ResponseEntity<>(pedidoCRUDRepo.findAll(query.build(), Pedido.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Reparto> getReparto(String id, HttpServletRequest request) {
        try {
            if (detectRestrictedAccess(request, AccessType.REPARTIDOR)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Repartidor> repartidor = repartidorCRUDRepo.findById(id, Repartidor.class);

        if (repartidor.isPresent()) {

            if (Objects.isNull(repartidor.get().getPedidoId())) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            Optional<Reparto> reparto = repartoCRUDRepo.findById(repartidor.get().getPedidoId(), Reparto.class);

            if (reparto.isPresent()) {
                log.info("The Reparto has been found");
                return new ResponseEntity<>(reparto.get(), HttpStatus.OK);
            }
        }

        log.warn("The Reparto hasn't been found");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Create factura.
     *
     * @param pedido the pedido
     */
    @Async
    void createFactura(Pedido pedido) {
        Factura factura = new Factura();
        factura.setId(pedido.getId());
        factura.setFecha(LocalDate.now());
        factura.setClienteId(pedido.getUsuarioId());

        double total = pedido.getPlatosPedidos()
                .stream()
                .mapToDouble(value -> getPlatoPriceById(value.getPlato()).doubleValue() * value.getCantidad())
                .sum();
        factura.setTotal(total);
        factura.setPlatosFactura(pedido.getPlatosPedidos().stream().map(value -> {
            PlatoFactura f = new PlatoFactura();
            f.setCantidad(value.getCantidad());
            f.setTotal(getPlatoPriceById(value.getPlato()).doubleValue() * f.getCantidad());
            f.setNombre(getPlatoNameById(value.getPlato()));
            return f;
        }).collect(Collectors.toList()));

        facturaRepo.save(factura);
        log.info("The Factura has been Created");
    }

    /**
     * Gets plato price by id.
     *
     * @param id the id
     * @return the plato price by id
     */
    private BigDecimal getPlatoPriceById(String id) {
        Optional<Plato> plato = platoCRUDRepo.findById(id, Plato.class);

        if (plato.isPresent()) {
            return plato.get().getPrecio();
        } else {
            return BigDecimal.ZERO;
        }

    }

    /**
     * Gets plato name by id.
     *
     * @param id the id
     * @return the plato name by id
     */
    private String getPlatoNameById(String id) {
        Optional<Plato> plato = platoCRUDRepo.findById(id, Plato.class);

        if (plato.isPresent()) {
            return plato.get().getNombre();
        } else {
            return Constants.EMPTY_PLATO_NAME;
        }
    }
}
