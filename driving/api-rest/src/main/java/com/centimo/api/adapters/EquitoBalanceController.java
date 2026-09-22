package com.centimo.api.adapters;

import com.centimo.api.EquitoBalanceApi;
import com.centimo.api.domain.models.EquitoBalance;
import com.centimo.api.dto.EquitoBalanceRequest;
import com.centimo.api.dto.EquitoBalanceResponse;
import com.centimo.api.mappers.EquitoBalanceApiMapper;
import com.centimo.api.ports.driving.EquitoBalanceDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EquitoBalanceController implements EquitoBalanceApi {

  private final EquitoBalanceDrivingPort equitoBalanceDrivingPort;
  private final EquitoBalanceApiMapper mapper;

  @Override
  public ResponseEntity<List<EquitoBalanceResponse>> listEquitoBalances(Integer limit, String order) {
    log.info("listEquitoBalances limit={} order={}", limit, order);
    List<EquitoBalanceResponse> balances = equitoBalanceDrivingPort.listar(limit, order).stream()
        .map(mapper::toEquitoBalanceResponse)
        .toList();
    return ResponseEntity.ok(balances);
  }

  @Override
  public ResponseEntity<EquitoBalanceResponse> createEquitoBalance(EquitoBalanceRequest equitoBalanceRequest) {
    log.info("createEquitoBalance");
    EquitoBalance modeloEntrada = mapper.toDomain(equitoBalanceRequest);
    EquitoBalance modeloCreado = equitoBalanceDrivingPort.crear(modeloEntrada);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toEquitoBalanceResponse(modeloCreado));
  }

  @Override
  public ResponseEntity<EquitoBalanceResponse> updateEquitoBalance(String id, EquitoBalanceRequest equitoBalanceRequest) {
    log.info("updateEquitoBalance id={}", id);
    EquitoBalance modeloEntrada = mapper.toDomain(equitoBalanceRequest);
    EquitoBalance modeloActualizado = equitoBalanceDrivingPort.actualizar(id, modeloEntrada);
    return ResponseEntity.ok(mapper.toEquitoBalanceResponse(modeloActualizado));
  }

  @Override
  public ResponseEntity<Void> deleteEquitoBalance(String id) {
    log.info("deleteEquitoBalance id={}", id);
    equitoBalanceDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}