package com.centimo.api.adapters;

import com.centimo.api.UrbanitaeBalanceApi;
import com.centimo.api.domain.models.UrbanitaeBalance;
import com.centimo.api.dto.UrbanitaeBalanceRequest;
import com.centimo.api.dto.UrbanitaeBalanceResponse;
import com.centimo.api.mappers.UrbanitaeBalanceApiMapper;
import com.centimo.api.ports.driving.UrbanitaeBalanceDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UrbanitaeBalanceController implements UrbanitaeBalanceApi {

  private final UrbanitaeBalanceDrivingPort urbanitaeBalanceDrivingPort;
  private final UrbanitaeBalanceApiMapper mapper;

  @Override
  public ResponseEntity<List<UrbanitaeBalanceResponse>> listUrbanitaeBalances(Integer limit, String order) {
    log.info("listUrbanitaeBalances limit={} order={}", limit, order);
    List<UrbanitaeBalanceResponse> balances = urbanitaeBalanceDrivingPort.listar(limit, order).stream()
        .map(mapper::toUrbanitaeBalanceResponse)
        .toList();
    return ResponseEntity.ok(balances);
  }

  @Override
  public ResponseEntity<UrbanitaeBalanceResponse> createUrbanitaeBalance(UrbanitaeBalanceRequest urbanitaeBalanceRequest) {
    log.info("createUrbanitaeBalance");
    UrbanitaeBalance modeloEntrada = mapper.toDomain(urbanitaeBalanceRequest);
    UrbanitaeBalance modeloCreado = urbanitaeBalanceDrivingPort.crear(modeloEntrada);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toUrbanitaeBalanceResponse(modeloCreado));
  }

  @Override
  public ResponseEntity<UrbanitaeBalanceResponse> updateUrbanitaeBalance(String id, UrbanitaeBalanceRequest urbanitaeBalanceRequest) {
    log.info("updateUrbanitaeBalance id={}", id);
    UrbanitaeBalance modeloEntrada = mapper.toDomain(urbanitaeBalanceRequest);
    UrbanitaeBalance modeloActualizado = urbanitaeBalanceDrivingPort.actualizar(id, modeloEntrada);
    return ResponseEntity.ok(mapper.toUrbanitaeBalanceResponse(modeloActualizado));
  }

  @Override
  public ResponseEntity<Void> deleteUrbanitaeBalance(String id) {
    log.info("deleteUrbanitaeBalance id={}", id);
    urbanitaeBalanceDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}