package com.centimo.api.adapters;

import com.centimo.api.RevolutBalanceApi;
import com.centimo.api.domain.models.RevolutBalance;
import com.centimo.api.dto.RevolutBalanceRequest;
import com.centimo.api.dto.RevolutBalanceResponse;
import com.centimo.api.mappers.RevolutBalanceApiMapper;
import com.centimo.api.ports.driving.RevolutBalanceDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RevolutBalanceController implements RevolutBalanceApi {

  private final RevolutBalanceDrivingPort revolutBalanceDrivingPort;
  private final RevolutBalanceApiMapper mapper;

  @Override
  public ResponseEntity<List<RevolutBalanceResponse>> listRevolutBalances(Integer limit, String order) {
    log.info("listRevolutBalances limit={} order={}", limit, order);
    List<RevolutBalanceResponse> balances = revolutBalanceDrivingPort.listar(limit, order).stream()
        .map(mapper::toRevolutBalanceResponse)
        .toList();
    return ResponseEntity.ok(balances);
  }

  @Override
  public ResponseEntity<RevolutBalanceResponse> createRevolutBalance(RevolutBalanceRequest revolutBalanceRequest) {
    log.info("createRevolutBalance");
    RevolutBalance modeloEntrada = mapper.toDomain(revolutBalanceRequest);
    RevolutBalance modeloCreado = revolutBalanceDrivingPort.crear(modeloEntrada);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRevolutBalanceResponse(modeloCreado));
  }

  @Override
  public ResponseEntity<RevolutBalanceResponse> updateRevolutBalance(String id, RevolutBalanceRequest revolutBalanceRequest) {
    log.info("updateRevolutBalance id={}", id);
    RevolutBalance modeloEntrada = mapper.toDomain(revolutBalanceRequest);
    RevolutBalance modeloActualizado = revolutBalanceDrivingPort.actualizar(id, modeloEntrada);
    return ResponseEntity.ok(mapper.toRevolutBalanceResponse(modeloActualizado));
  }

  @Override
  public ResponseEntity<Void> deleteRevolutBalance(String id) {
    log.info("deleteRevolutBalance id={}", id);
    revolutBalanceDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}