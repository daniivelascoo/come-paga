package com.example.comepaga.model.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@Document(collection = "facturas")
public class Factura {

    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("fecha_creacion")
    private LocalDate fecha;

    @JsonProperty("cliente_id")
    private String clienteId;

    @JsonProperty("total")
    private Double total;

    @JsonProperty("paltos_factura")
    private List<PlatoFactura> platosFactura;
}
