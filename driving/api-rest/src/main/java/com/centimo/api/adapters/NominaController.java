package com.centimo.api.adapters;

import com.centimo.api.NominaApi;
import com.centimo.api.domain.models.Nomina;
import com.centimo.api.dto.NominaRequest;
import com.centimo.api.dto.NominaResponse;
import com.centimo.api.mappers.NominaApiMapper;
import com.centimo.api.ports.driving.NominaDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NominaController implements NominaApi {

  private final NominaDrivingPort nominaDrivingPort;
  private final NominaApiMapper mapper;

  @Override
  public ResponseEntity<NominaResponse> getNomina(String mes) {
    log.info("getNomina mes={}", mes);
    return ResponseEntity.of(nominaDrivingPort.obtener(mes).map(mapper::toNominaResponse));
  }

  @Override
  public ResponseEntity<NominaResponse> createNomina(NominaRequest nominaRequest) {
    log.info("createNomina");
    Nomina modeloCreado = nominaDrivingPort.guardar(mapper.toDomain(nominaRequest));
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toNominaResponse(modeloCreado));
  }

  @Override
  public ResponseEntity<NominaResponse> updateNomina(String mes, NominaRequest nominaRequest) {
    log.info("updateNomina mes={}", mes);
    Nomina modeloActualizado = nominaDrivingPort.actualizar(mes, mapper.toDomain(nominaRequest));
    return ResponseEntity.ok(mapper.toNominaResponse(modeloActualizado));
  }

  @Override
  public ResponseEntity<Void> deleteNomina(String mes) {
    log.info("deleteNomina mes={}", mes);
    nominaDrivingPort.eliminar(mes);
    return ResponseEntity.noContent().build();
  }
}