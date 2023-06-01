package com.example.comepaga.model.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PlatoPedido {

    @JsonProperty("id_plato")
    private String plato;

    @JsonProperty("cantidad")
    private Integer cantidad;
}
