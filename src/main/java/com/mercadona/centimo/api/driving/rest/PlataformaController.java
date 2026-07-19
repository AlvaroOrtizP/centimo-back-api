package com.mercadona.centimo.api.driving.rest;

import com.mercadona.centimo.api.application.services.PlataformaUseCase;
import com.mercadona.centimo.api.driven.jpa.Plataforma;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plataformas")
@RequiredArgsConstructor
public class PlataformaController {

  private final PlataformaUseCase plataformaUseCase;

  @GetMapping
  public ResponseEntity<List<Plataforma>> buscarTodas() {
    return ResponseEntity.ok(plataformaUseCase.buscarTodas());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Plataforma> buscarPorId(@PathVariable String id) {
    return ResponseEntity.ok(plataformaUseCase.buscarPorId(id));
  }

  @PostMapping
  public ResponseEntity<Plataforma> crear(@RequestBody Plataforma plataforma) {
    return ResponseEntity.status(HttpStatus.CREATED).body(plataformaUseCase.crear(plataforma));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Plataforma> actualizar(@PathVariable String id, @RequestBody Plataforma plataforma) {
    return ResponseEntity.ok(plataformaUseCase.actualizar(id, plataforma));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    plataformaUseCase.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}