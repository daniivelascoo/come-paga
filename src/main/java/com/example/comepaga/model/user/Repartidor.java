package com.example.comepaga.model.user;

import com.example.comepaga.model.delivery.HistoricoRepartos;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "usuarios")
@Data
public class Repartidor extends Usuario {

    @JsonProperty("pedido_id")
    private String pedidoId;

    @JsonProperty("historico_repartos")
    private HistoricoRepartos historicoRepartos;
}
