package com.centimo.api.adapters;

import com.centimo.api.domain.models.FuenteIngreso;
import com.centimo.api.dto.CreateFuenteIngresoRequest;
import com.centimo.api.dto.FuenteIngresoDto;
import com.centimo.api.mappers.FuenteIngresoApiMapper;
import com.centimo.api.ports.driving.FuenteIngresoDrivingPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingresos")
@RequiredArgsConstructor
public class FuenteIngresoController {

  private final FuenteIngresoDrivingPort fuenteIngresoDrivingPort;
  private final FuenteIngresoApiMapper mapper;

  /**
   * Usado en: Vista Mensual (lista de ingresos),
   * Entrada Datos (formulario de ingresos),
   * Nómina (registrar ingreso de distribución de sueldo).
   */
  @GetMapping
  public ResponseEntity<List<FuenteIngresoDto>> buscarTodas(
      @RequestParam(required = false) String instantaneaId) {
    List<FuenteIngresoDto> dtos = fuenteIngresoDrivingPort.buscarTodas(instantaneaId).stream()
      .map(mapper::toDto)
      .toList();
    return ResponseEntity.ok(dtos);
  }

  /**
   * Usado en: Entrada Datos (registrar ingreso),
   * Nómina (registrar ingreso de distribución de sueldo).
   * Al crear, se incrementa instantánea.ingresos de forma automática.
   */
  @PostMapping
  public ResponseEntity<FuenteIngresoDto> crear(@Valid @RequestBody CreateFuenteIngresoRequest request) {
    FuenteIngreso ingreso = fuenteIngresoDrivingPort.crear(mapper.toDomain(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(ingreso));
  }

  /**
   * Usado en: Vista Mensual, Entrada Datos, Nómina (eliminar ingreso).
   * Al eliminar, se decrementa instantánea.ingresos de forma automática.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    fuenteIngresoDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
