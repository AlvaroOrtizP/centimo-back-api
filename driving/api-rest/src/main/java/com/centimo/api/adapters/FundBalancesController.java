package com.centimo.api.adapters;

import com.centimo.api.FundBalancesApi;
import com.centimo.api.domain.models.BalanceFondo;
import com.centimo.api.dto.FundBalance;
import com.centimo.api.dto.FundBalanceCreate;
import com.centimo.api.dto.FundBalanceUpdate;
import com.centimo.api.mappers.FundBalanceApiMapper;
import com.centimo.api.ports.driving.FundBalanceDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FundBalancesController implements FundBalancesApi {

  private final FundBalanceDrivingPort fundBalanceDrivingPort;
  private final FundBalanceApiMapper mapper;

  @Override
  public ResponseEntity<List<FundBalance>> listFundBalances(Integer year, Integer month) {
    log.info("listFundBalances year={} month={}", year, month);
    List<FundBalance> balances = fundBalanceDrivingPort.listarPorMes(year, month).stream()
        .map(mapper::toFundBalanceResponse)
        .toList();
    return ResponseEntity.ok(balances);
  }

  @Override
  public ResponseEntity<FundBalance> createFundBalance(FundBalanceCreate fundBalanceCreate) {
    log.info("createFundBalance");
    BalanceFondo modeloEntrada = mapper.toDomain(fundBalanceCreate);
    BalanceFondo modeloCreado = fundBalanceDrivingPort.crear(modeloEntrada);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toFundBalanceResponse(modeloCreado));
  }

  @Override
  public ResponseEntity<FundBalance> updateFundBalance(String id, FundBalanceUpdate fundBalanceUpdate) {
    log.info("updateFundBalance id={}", id);
    BalanceFondo modeloEntrada = mapper.toDomain(fundBalanceUpdate);
    BalanceFondo modeloActualizado = fundBalanceDrivingPort.actualizar(id, modeloEntrada);
    return ResponseEntity.ok(mapper.toFundBalanceResponse(modeloActualizado));
  }

  @Override
  public ResponseEntity<Void> deleteFundBalance(String id) {
    log.info("deleteFundBalance id={}", id);
    fundBalanceDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}