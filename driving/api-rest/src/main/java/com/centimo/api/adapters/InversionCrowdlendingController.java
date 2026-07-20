package com.centimo.api.adapters;

import com.centimo.api.domain.models.InversionCrowdlending;
import com.centimo.api.dto.CreateInversionCrowdlendingRequest;
import com.centimo.api.dto.InversionCrowdlendingDto;
import com.centimo.api.mappers.InversionCrowdlendingApiMapper;
import com.centimo.api.ports.driving.InversionCrowdlendingDrivingPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/crowdlending")
@RequiredArgsConstructor
public class InversionCrowdlendingController {

  private final InversionCrowdlendingDrivingPort inversionDrivingPort;
  private final InversionCrowdlendingApiMapper mapper;

  @GetMapping
  public ResponseEntity<List<InversionCrowdlendingDto>> buscarTodas(
      @RequestParam(required = false) String plataformaId) {
    List<InversionCrowdlendingDto> dtos = inversionDrivingPort.buscarTodas(plataformaId).stream()
      .map(mapper::toDto)
      .toList();
    return ResponseEntity.ok(dtos);
  }

  @PostMapping
  public ResponseEntity<InversionCrowdlendingDto> crear(
      @Valid @RequestBody CreateInversionCrowdlendingRequest request) {
    InversionCrowdlending inversion = inversionDrivingPort.crear(mapper.toDomain(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(inversion));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    inversionDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
