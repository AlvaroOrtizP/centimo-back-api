package com.centimo.api.adapters;

import com.centimo.api.domain.models.PosicionInversion;
import com.centimo.api.dto.CreatePosicionInversionRequest;
import com.centimo.api.dto.PosicionInversionDto;
import com.centimo.api.mappers.PosicionInversionApiMapper;
import com.centimo.api.ports.driving.PosicionInversionDrivingPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posiciones")
@RequiredArgsConstructor
public class PosicionInversionController {

  private final PosicionInversionDrivingPort posicionDrivingPort;
  private final PosicionInversionApiMapper mapper;

  @GetMapping
  public ResponseEntity<List<PosicionInversionDto>> buscarTodas(
      @RequestParam(required = false) String instantaneaId) {
    List<PosicionInversionDto> dtos = posicionDrivingPort.buscarTodas(instantaneaId).stream()
      .map(mapper::toDto)
      .toList();
    return ResponseEntity.ok(dtos);
  }

  @PostMapping
  public ResponseEntity<PosicionInversionDto> crear(@Valid @RequestBody CreatePosicionInversionRequest request) {
    PosicionInversion posicion = posicionDrivingPort.crear(mapper.toDomain(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(posicion));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    posicionDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
