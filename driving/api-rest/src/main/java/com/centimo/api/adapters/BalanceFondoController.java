package com.centimo.api.adapters;

import com.centimo.api.domain.models.BalanceFondo;
import com.centimo.api.dto.BalanceFondoDto;
import com.centimo.api.dto.CreateBalanceFondoRequest;
import com.centimo.api.mappers.BalanceFondoApiMapper;
import com.centimo.api.ports.driving.BalanceFondoDrivingPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/balances-fondo")
@RequiredArgsConstructor
public class BalanceFondoController {

  private final BalanceFondoDrivingPort balanceDrivingPort;
  private final BalanceFondoApiMapper mapper;

  @GetMapping
  public ResponseEntity<List<BalanceFondoDto>> buscarTodos(
      @RequestParam(required = false) Integer anio,
      @RequestParam(required = false) Integer mes) {
    List<BalanceFondoDto> dtos = balanceDrivingPort.buscarTodos(anio, mes).stream()
      .map(mapper::toDto)
      .toList();
    return ResponseEntity.ok(dtos);
  }

  @PostMapping
  public ResponseEntity<BalanceFondoDto> crear(@Valid @RequestBody CreateBalanceFondoRequest request) {
    BalanceFondo balance = balanceDrivingPort.crear(mapper.toDomain(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(balance));
  }

  @PutMapping("/{id}")
  public ResponseEntity<BalanceFondoDto> actualizar(
      @PathVariable String id,
      @Valid @RequestBody CreateBalanceFondoRequest request) {
    BalanceFondo balance = balanceDrivingPort.actualizar(id, mapper.toDomain(request));
    return ResponseEntity.ok(mapper.toDto(balance));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    balanceDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
