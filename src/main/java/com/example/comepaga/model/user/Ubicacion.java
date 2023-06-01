package com.example.comepaga.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Ubicacion {

    @JsonProperty("localizacion")
    private String localizacion;

    @JsonProperty("direccion")
    private String direccion;
}
