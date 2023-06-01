package com.example.comepaga.model.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class HistoricoPedidos {

    @JsonProperty("pedidos")
    private List<Pedido> pedidos;
}
