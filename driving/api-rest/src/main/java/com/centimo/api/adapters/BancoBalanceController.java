package com.centimo.api.adapters;

import com.centimo.api.BancoBalanceApi;
import com.centimo.api.domain.models.BancoBalance;
import com.centimo.api.dto.BancoBalanceRequest;
import com.centimo.api.dto.BancoBalanceResponse;
import com.centimo.api.mappers.BancoBalanceApiMapper;
import com.centimo.api.ports.driving.BancoBalanceDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BancoBalanceController implements BancoBalanceApi {

  private final BancoBalanceDrivingPort bancoBalanceDrivingPort;
  private final BancoBalanceApiMapper mapper;

  @Override
  public ResponseEntity<List<BancoBalanceResponse>> listBancoBalances(String entidad, Integer limit, String order) {
    log.info("listBancoBalances entidad={} limit={} order={}", entidad, limit, order);
    List<BancoBalanceResponse> balances = bancoBalanceDrivingPort.listarPorEntidad(entidad, limit, order).stream()
        .map(mapper::toBancoBalanceResponse)
        .toList();
    return ResponseEntity.ok(balances);
  }

  @Override
  public ResponseEntity<BancoBalanceResponse> createBancoBalance(BancoBalanceRequest bancoBalanceRequest) {
    log.info("createBancoBalance");
    BancoBalance modeloEntrada = mapper.toDomain(bancoBalanceRequest);
    BancoBalance modeloCreado = bancoBalanceDrivingPort.crear(modeloEntrada);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toBancoBalanceResponse(modeloCreado));
  }

  @Override
  public ResponseEntity<BancoBalanceResponse> updateBancoBalance(String id, BancoBalanceRequest bancoBalanceRequest) {
    log.info("updateBancoBalance id={}", id);
    BancoBalance modeloEntrada = mapper.toDomain(bancoBalanceRequest);
    BancoBalance modeloActualizado = bancoBalanceDrivingPort.actualizar(id, modeloEntrada);
    return ResponseEntity.ok(mapper.toBancoBalanceResponse(modeloActualizado));
  }

  @Override
  public ResponseEntity<Void> deleteBancoBalance(String id) {
    log.info("deleteBancoBalance id={}", id);
    bancoBalanceDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}