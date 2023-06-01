package com.example.comepaga.model.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class HistoricoRepartos {

    @JsonProperty("repartos")
    private List<Reparto> repartos;
}
