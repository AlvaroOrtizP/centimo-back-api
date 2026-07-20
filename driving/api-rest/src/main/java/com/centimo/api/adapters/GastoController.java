package com.centimo.api.adapters;

import com.centimo.api.domain.models.Gasto;
import com.centimo.api.dto.CreateGastoRequest;
import com.centimo.api.dto.GastoDto;
import com.centimo.api.mappers.GastoApiMapper;
import com.centimo.api.ports.driving.GastoDrivingPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gastos")
@RequiredArgsConstructor
public class GastoController {

  private final GastoDrivingPort gastoDrivingPort;
  private final GastoApiMapper mapper;

  /**
   * Usado en: Vista Mensual (gráfico de categorías),
   * Entrada Datos (formulario de gastos).
   */
  @GetMapping
  public ResponseEntity<List<GastoDto>> buscarTodos(
      @RequestParam(required = false) String instantaneaId) {
    List<GastoDto> dtos = gastoDrivingPort.buscarTodos(instantaneaId).stream()
      .map(mapper::toDto)
      .toList();
    return ResponseEntity.ok(dtos);
  }

  /**
   * Usado en: Entrada Datos (registrar gasto).
   * Al crear, se incrementa instantánea.gastos de forma automática.
   */
  @PostMapping
  public ResponseEntity<GastoDto> crear(@Valid @RequestBody CreateGastoRequest request) {
    Gasto gasto = gastoDrivingPort.crear(mapper.toDomain(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(gasto));
  }

  /**
   * Usado en: Vista Mensual, Entrada Datos (eliminar gasto).
   * Al eliminar, se decrementa instantánea.gastos de forma automática.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    gastoDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
