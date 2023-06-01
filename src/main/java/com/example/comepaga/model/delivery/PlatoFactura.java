package com.example.comepaga.model.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PlatoFactura {

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("cantidad")
    private Integer cantidad;

    @JsonProperty("total")
    private Double total;
}
