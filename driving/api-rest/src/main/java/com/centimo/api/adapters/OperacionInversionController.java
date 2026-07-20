package com.centimo.api.adapters;

import com.centimo.api.domain.models.OperacionInversion;
import com.centimo.api.dto.CreateOperacionInversionRequest;
import com.centimo.api.dto.OperacionInversionDto;
import com.centimo.api.mappers.OperacionInversionApiMapper;
import com.centimo.api.ports.driving.OperacionInversionDrivingPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operaciones")
@RequiredArgsConstructor
public class OperacionInversionController {

  private final OperacionInversionDrivingPort operacionDrivingPort;
  private final OperacionInversionApiMapper mapper;

  @GetMapping
  public ResponseEntity<List<OperacionInversionDto>> buscarTodas(
      @RequestParam(required = false) String cuentaId) {
    List<OperacionInversionDto> dtos = operacionDrivingPort.buscarTodas(cuentaId).stream()
      .map(mapper::toDto)
      .toList();
    return ResponseEntity.ok(dtos);
  }

  @PostMapping
  public ResponseEntity<OperacionInversionDto> crear(@Valid @RequestBody CreateOperacionInversionRequest request) {
    OperacionInversion operacion = operacionDrivingPort.crear(mapper.toDomain(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(operacion));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    operacionDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
