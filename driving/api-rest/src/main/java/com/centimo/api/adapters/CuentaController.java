package com.centimo.api.adapters;

import com.centimo.api.domain.models.Cuenta;
import com.centimo.api.dto.CreateCuentaRequest;
import com.centimo.api.dto.CuentaDto;
import com.centimo.api.dto.UpdateCuentaRequest;
import com.centimo.api.mappers.CuentaApiMapper;
import com.centimo.api.ports.driving.CuentaDrivingPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

  private final CuentaDrivingPort cuentaDrivingPort;
  private final CuentaApiMapper mapper;

  @GetMapping
  public ResponseEntity<List<CuentaDto>> buscarTodas(
      @RequestParam(required = false) String plataformaId) {
    List<CuentaDto> dtos = cuentaDrivingPort.buscarTodas(plataformaId).stream()
      .map(mapper::toDto)
      .toList();
    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CuentaDto> buscarPorId(@PathVariable String id) {
    Cuenta cuenta = cuentaDrivingPort.buscarPorId(id);
    return ResponseEntity.ok(mapper.toDto(cuenta));
  }

  @PostMapping
  public ResponseEntity<CuentaDto> crear(@Valid @RequestBody CreateCuentaRequest request) {
    Cuenta cuenta = cuentaDrivingPort.crear(mapper.toDomain(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(cuenta));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CuentaDto> actualizar(
      @PathVariable String id,
      @Valid @RequestBody UpdateCuentaRequest request) {
    Cuenta cuenta = cuentaDrivingPort.actualizar(id, mapper.toDomain(request));
    return ResponseEntity.ok(mapper.toDto(cuenta));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    cuentaDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
