package com.example.comepaga.web;

import com.example.comepaga.model.restaurant.Plato;
import com.example.comepaga.service.impl.PlatoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/come-paga/plato")
public class PlatoRestController {

    private final PlatoServiceImpl service;

    @Autowired
    public PlatoRestController(PlatoServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/{plato_id}")
    public ResponseEntity<Plato> get(@PathVariable("plato_id") String platoId) {
        log.info("Try Get Plato By id {}", platoId);
        return this.service.get(platoId);
    }

}
