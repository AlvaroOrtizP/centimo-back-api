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

  /**
   * Usado en: Dashboard (colores y orden), Vista Mensual (nombre por cuenta),
   * Tendencias (distribución por plataforma), Operaciones (filtro y P&L por plataforma),
   * Entrada Datos (selector de plataforma), Nómina (destino de distribución),
   * Detalle Plataforma (nombre, tipo, color).
   */
  @GetMapping
  public ResponseEntity<List<PlataformaDto>> buscarTodas() {
    List<PlataformaDto> dtos = plataformaDrivingPort.buscarTodas().stream()
      .map(mapper::toDto)
      .toList();
    return ResponseEntity.ok(dtos);
  }

  /** Reservado para futuras pantallas de detalle de plataforma. */
  @GetMapping("/{id}")
  public ResponseEntity<PlataformaDto> buscarPorId(@PathVariable String id) {
    Plataforma plataforma = plataformaDrivingPort.buscarPorId(id);
    return ResponseEntity.ok(mapper.toDto(plataforma));
  }

  /** Reservado para futura pantalla de admin de plataformas. */
  @PostMapping
  public ResponseEntity<PlataformaDto> crear(@Valid @RequestBody CreatePlataformaRequest request) {
    Plataforma plataforma = plataformaDrivingPort.crear(mapper.toDomain(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(plataforma));
  }

  /** Reservado para futura pantalla de admin de plataformas. */
  @PutMapping("/{id}")
  public ResponseEntity<PlataformaDto> actualizar(
      @PathVariable String id,
      @Valid @RequestBody UpdatePlataformaRequest request) {
    Plataforma plataforma = plataformaDrivingPort.actualizar(id, mapper.toDomain(request));
    return ResponseEntity.ok(mapper.toDto(plataforma));
  }

  /** Reservado para futura pantalla de admin de plataformas. */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    plataformaDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
