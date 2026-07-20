package com.centimo.api.adapters;

import com.centimo.api.domain.models.Compromiso;
import com.centimo.api.dto.AlertaCompromisoDto;
import com.centimo.api.dto.CompromisoDto;
import com.centimo.api.dto.CreateCompromisoRequest;
import com.centimo.api.mappers.CompromisoApiMapper;
import com.centimo.api.ports.driving.CompromisoDrivingPort;
import com.centimo.api.usecases.CompromisoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compromisos")
@RequiredArgsConstructor
public class CompromisoController {

  private final CompromisoDrivingPort compromisoDrivingPort;
  private final CompromisoUseCase compromisoUseCase;
  private final CompromisoApiMapper mapper;

  @GetMapping
  public ResponseEntity<List<CompromisoDto>> buscarTodos(
      @RequestParam(required = false) Integer mes,
      @RequestParam(required = false) Integer anio) {
    List<Compromiso> compromisos;
    if (mes != null) {
      compromisos = compromisoDrivingPort.buscarPorMes(mes);
    } else {
      compromisos = compromisoDrivingPort.buscarTodos();
    }
    List<CompromisoDto> dtos = compromisos.stream()
      .map(mapper::toDto)
      .toList();
    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CompromisoDto> buscarPorId(@PathVariable String id) {
    CompromisoDto dto = mapper.toDto(compromisoDrivingPort.buscarPorId(id));
    return ResponseEntity.ok(dto);
  }

  @GetMapping("/alertas")
  public ResponseEntity<List<AlertaCompromisoDto>> alertas(
      @RequestParam Integer anio,
      @RequestParam Integer mes) {
    List<AlertaCompromisoDto> alertas = compromisoUseCase.alertas(anio, mes).stream()
      .map(a -> AlertaCompromisoDto.builder()
        .year(a.anio())
        .month(a.mes())
        .compromisos(a.compromisos().stream().map(mapper::toDto).toList())
        .total(a.total())
        .build())
      .toList();
    return ResponseEntity.ok(alertas);
  }

  @PostMapping
  public ResponseEntity<CompromisoDto> crear(@Valid @RequestBody CreateCompromisoRequest request) {
    Compromiso compromiso = compromisoDrivingPort.crear(mapper.toDomain(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(compromiso));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CompromisoDto> actualizar(
      @PathVariable String id,
      @Valid @RequestBody CreateCompromisoRequest request) {
    Compromiso compromiso = compromisoDrivingPort.actualizar(id, mapper.toDomain(request));
    return ResponseEntity.ok(mapper.toDto(compromiso));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    compromisoDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
