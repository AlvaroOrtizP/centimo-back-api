package com.centimo.api.adapters;

import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.dto.CreateInstantaneaRequest;
import com.centimo.api.dto.InstantaneaDto;
import com.centimo.api.dto.UpdateInstantaneaRequest;
import com.centimo.api.dto.UpsertInstantaneaRequest;
import com.centimo.api.mappers.InstantaneaApiMapper;
import com.centimo.api.ports.driving.InstantaneaDrivingPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instantaneas")
@RequiredArgsConstructor
public class InstantaneaController {

  private final InstantaneaDrivingPort instantaneaDrivingPort;
  private final InstantaneaApiMapper mapper;

  /**
   * Usado en: Dashboard (saldos, ingresos y gastos del mes),
   * Vista Mensual (desglose por cuenta del mes seleccionado),
   * Tendencias (histórico de saldos para gráficos de evolución),
   * Entrada Datos (snapshots por plataforma),
   * Nómina (buscar snapshot para registrar ingresos),
   * Detalle Plataforma (historial de saldos).
   */
  @GetMapping
  public ResponseEntity<List<InstantaneaDto>> buscarTodas(
      @RequestParam(required = false) Integer anio,
      @RequestParam(required = false) Integer mes,
      @RequestParam(required = false) String cuentaId) {
    List<InstantaneaDto> dtos = instantaneaDrivingPort.buscarTodas(anio, mes, cuentaId).stream()
      .map(mapper::toDto)
      .toList();
    return ResponseEntity.ok(dtos);
  }

  /** Reservado para futuras pantallas de detalle de instantánea. */
  @GetMapping("/{id}")
  public ResponseEntity<InstantaneaDto> buscarPorId(@PathVariable String id) {
    InstantaneaMensual instantanea = instantaneaDrivingPort.buscarPorId(id);
    return ResponseEntity.ok(mapper.toDto(instantanea));
  }

  /** Reservado para uso interno o scripts de migración de datos. */
  @PostMapping
  public ResponseEntity<InstantaneaDto> crear(@Valid @RequestBody CreateInstantaneaRequest request) {
    InstantaneaMensual instantanea = instantaneaDrivingPort.crear(mapper.toDomain(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(instantanea));
  }

  /**
   * Usado en: Entrada Datos (registrar saldo mensual de una cuenta),
   * Nómina (registrar ingresos de la distribución de sueldo).
   * Crea la instantánea si no existe, o actualiza saldo e ingresos de forma incremental.
   */
  @PostMapping("/upsert")
  public ResponseEntity<InstantaneaDto> upsert(@Valid @RequestBody UpsertInstantaneaRequest request) {
    InstantaneaMensual instantanea = instantaneaDrivingPort.upsert(
      request.getAccountId(),
      request.getYear(),
      request.getMonth(),
      request.getBalance(),
      request.getDeltaIncome(),
      request.getExpenses()
    );
    return ResponseEntity.ok(mapper.toDto(instantanea));
  }

  /** Reservado para uso interno o scripts de migración de datos. */
  @PutMapping("/{id}")
  public ResponseEntity<InstantaneaDto> actualizar(
      @PathVariable String id,
      @Valid @RequestBody UpdateInstantaneaRequest request) {
    InstantaneaMensual instantanea = instantaneaDrivingPort.actualizar(id, mapper.toDomain(request));
    return ResponseEntity.ok(mapper.toDto(instantanea));
  }

  /** Reservado para uso interno o scripts de migración de datos. */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    instantaneaDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
