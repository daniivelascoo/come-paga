package com.example.comepaga.model.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Document(collection = "platos")
@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plato {

    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("disponible")
    private Boolean disponible;

    @JsonProperty("precio")
    private BigDecimal precio;
}
