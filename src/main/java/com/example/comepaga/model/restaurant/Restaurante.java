package com.example.comepaga.model.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Document(collection = "restaurantes")
@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurante {

    @Id
    @JsonProperty("id")
    private String nombre;

    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("categoria")
    private String categoria;

    @JsonProperty("valoracion_media")
    private Integer valoracionMedia;

    @JsonProperty("precio_medio")
    private Integer precioMedio;

    @JsonProperty("valoraciones")
    private List<Integer> valoraciones;

    @Transient
    @JsonProperty("platos_disponibles_crear")
    private List<Plato> platosCrear;

    @JsonProperty("platos_disponibles")
    private List<String> platosCreados;

    public void calculateHalfPrice() {
        BigDecimal precioMedio = BigDecimal.ZERO;

        for (Plato plato : this.platosCrear) {
            precioMedio = precioMedio.add(plato.getPrecio());
        }

        precioMedio = precioMedio.divide(BigDecimal.valueOf(this.platosCrear.size()), RoundingMode.HALF_UP);

        if (precioMedio.doubleValue() >= 10) this.setPrecioMedio(1);
        if (precioMedio.doubleValue() > 10 && precioMedio.doubleValue() <= 15) this.setPrecioMedio(2);
        if (precioMedio.doubleValue() > 16 && precioMedio.doubleValue() <= 20) this.setPrecioMedio(3);
        if (precioMedio.doubleValue() > 20) this.setPrecioMedio(4);
    }

    public void calculateMedianRating() {
        BigDecimal suma = this.valoraciones.stream()
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal result = suma.divide(BigDecimal.valueOf(this.valoraciones.size()), RoundingMode.HALF_UP);
        this.setValoracionMedia(result.intValue());
    }

}
