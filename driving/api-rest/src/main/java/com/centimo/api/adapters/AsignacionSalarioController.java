package com.centimo.api.adapters;

import com.centimo.api.domain.models.AsignacionSalario;
import com.centimo.api.dto.AsignacionSalarioDto;
import com.centimo.api.dto.CreateAsignacionSalarioRequest;
import com.centimo.api.mappers.AsignacionSalarioApiMapper;
import com.centimo.api.ports.driving.AsignacionSalarioDrivingPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asignaciones-salario")
@RequiredArgsConstructor
public class AsignacionSalarioController {

  private final AsignacionSalarioDrivingPort asignacionDrivingPort;
  private final AsignacionSalarioApiMapper mapper;

  @GetMapping
  public ResponseEntity<List<AsignacionSalarioDto>> buscarTodas(
      @RequestParam(required = false) Integer anio,
      @RequestParam(required = false) Integer mes) {
    List<AsignacionSalarioDto> dtos = asignacionDrivingPort.buscarTodas(anio, mes).stream()
      .map(mapper::toDto)
      .toList();
    return ResponseEntity.ok(dtos);
  }

  @PostMapping
  public ResponseEntity<AsignacionSalarioDto> crear(
      @Valid @RequestBody CreateAsignacionSalarioRequest request) {
    AsignacionSalario asignacion = asignacionDrivingPort.crear(mapper.toDomain(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(asignacion));
  }

  @PutMapping("/{id}")
  public ResponseEntity<AsignacionSalarioDto> actualizar(
      @PathVariable String id,
      @Valid @RequestBody CreateAsignacionSalarioRequest request) {
    AsignacionSalario asignacion = asignacionDrivingPort.actualizar(id, mapper.toDomain(request));
    return ResponseEntity.ok(mapper.toDto(asignacion));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    asignacionDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
