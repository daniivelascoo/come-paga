package com.example.comepaga.model.delivery;

import com.example.comepaga.model.restaurant.PlatoPedido;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@Document(collection = "pedidos")
@Data
public class Pedido {

    @JsonProperty("id")
    private String id;

    @JsonProperty("cliente_id")
    private String usuarioId;

    @JsonProperty("repartidor_id")
    private String repartidorId;

    @JsonProperty("restaurante_id")
    private String restauranteId;

    @JsonProperty("ubicacion_reparto")
    private String ubicacionReparto;

    @JsonProperty("fecha_inicio")
    private LocalDateTime fechaInicio;

    @JsonProperty("fecha_entrega")
    private LocalDateTime fechaEntrega;

    @JsonProperty("platos_pedidos")
    private List<PlatoPedido> platosPedidos;

    @JsonProperty("codigo_estado_pedido")
    private String codigoEstadoPedido;

}
