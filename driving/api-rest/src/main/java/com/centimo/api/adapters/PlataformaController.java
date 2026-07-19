package com.centimo.api.adapters;

import com.centimo.api.domain.models.Plataforma;
import com.centimo.api.dto.CreatePlataformaRequest;
import com.centimo.api.dto.PlataformaDto;
import com.centimo.api.dto.UpdatePlataformaRequest;
import com.centimo.api.mappers.PlataformaApiMapper;
import com.centimo.api.ports.driving.PlataformaDrivingPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plataformas")
@RequiredArgsConstructor
public class PlataformaController {

  private final PlataformaDrivingPort plataformaDrivingPort;
  private final PlataformaApiMapper mapper;

  @GetMapping
  public ResponseEntity<List<PlataformaDto>> buscarTodas() {
    List<PlataformaDto> dtos = plataformaDrivingPort.buscarTodas().stream()
      .map(mapper::toDto)
      .toList();
    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PlataformaDto> buscarPorId(@PathVariable String id) {
    Plataforma plataforma = plataformaDrivingPort.buscarPorId(id);
    return ResponseEntity.ok(mapper.toDto(plataforma));
  }

  @PostMapping
  public ResponseEntity<PlataformaDto> crear(@Valid @RequestBody CreatePlataformaRequest request) {
    Plataforma plataforma = plataformaDrivingPort.crear(mapper.toDomain(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(plataforma));
  }

  @PutMapping("/{id}")
  public ResponseEntity<PlataformaDto> actualizar(
      @PathVariable String id,
      @Valid @RequestBody UpdatePlataformaRequest request) {
    Plataforma plataforma = plataformaDrivingPort.actualizar(id, mapper.toDomain(request));
    return ResponseEntity.ok(mapper.toDto(plataforma));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    plataformaDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
