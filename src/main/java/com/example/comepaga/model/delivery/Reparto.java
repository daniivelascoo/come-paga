package com.example.comepaga.model.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "repartos")
@Data
public class Reparto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("fecha_recogido")
    private LocalDateTime fechaRecogido;

    @JsonProperty("fecha_entregado")
    private LocalDateTime fechaEntregado;

    public static Reparto create(Pedido pedido){
        Reparto reparto = new Reparto();
        reparto.setId(pedido.getId());
        reparto.setFechaRecogido(LocalDateTime.now());

        return reparto;
    }
}
