package com.example.comepaga.model.user;

import com.example.comepaga.model.delivery.HistoricoPedidos;
import com.example.comepaga.model.delivery.Pedido;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "usuarios")
@Data
public class Cliente extends Usuario {

    @JsonProperty("ubicaciones")
    private List<Ubicacion> ubicaciones;

    @JsonProperty("ubicacion_actual")
    private Ubicacion ubicacionActual;

    @JsonProperty("historico_pedidos")
    private HistoricoPedidos historicoPedidos;

    public void addNewPedido(Pedido p){
        HistoricoPedidos h = this.historicoPedidos;

        if (Objects.isNull(h)) h = new HistoricoPedidos();
        if (Objects.isNull(h.getPedidos())) h.setPedidos(new ArrayList<>());

        h.getPedidos().add(p);
    }
}
