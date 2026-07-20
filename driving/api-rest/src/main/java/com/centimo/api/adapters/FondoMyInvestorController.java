package com.centimo.api.adapters;

import com.centimo.api.domain.models.FondoMyInvestor;
import com.centimo.api.dto.CreateFondoMyInvestorRequest;
import com.centimo.api.dto.FondoMyInvestorDto;
import com.centimo.api.mappers.FondoMyInvestorApiMapper;
import com.centimo.api.ports.driving.FondoMyInvestorDrivingPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fondos-myinvestor")
@RequiredArgsConstructor
public class FondoMyInvestorController {

  private final FondoMyInvestorDrivingPort fondoDrivingPort;
  private final FondoMyInvestorApiMapper mapper;

  @GetMapping
  public ResponseEntity<List<FondoMyInvestorDto>> buscarTodos() {
    List<FondoMyInvestorDto> dtos = fondoDrivingPort.buscarTodos().stream()
      .map(mapper::toDto)
      .toList();
    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<FondoMyInvestorDto> buscarPorId(@PathVariable String id) {
    FondoMyInvestorDto dto = mapper.toDto(fondoDrivingPort.buscarPorId(id));
    return ResponseEntity.ok(dto);
  }

  @PostMapping
  public ResponseEntity<FondoMyInvestorDto> crear(
      @Valid @RequestBody CreateFondoMyInvestorRequest request) {
    FondoMyInvestor fondo = fondoDrivingPort.crear(mapper.toDomain(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(fondo));
  }

  @PutMapping("/{id}")
  public ResponseEntity<FondoMyInvestorDto> actualizar(
      @PathVariable String id,
      @Valid @RequestBody CreateFondoMyInvestorRequest request) {
    FondoMyInvestor fondo = fondoDrivingPort.actualizar(id, mapper.toDomain(request));
    return ResponseEntity.ok(mapper.toDto(fondo));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    fondoDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
