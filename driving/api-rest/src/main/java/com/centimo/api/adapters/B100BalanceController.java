package com.centimo.api.adapters;

import com.centimo.api.B100BalanceApi;
import com.centimo.api.domain.enums.TipoSubcuentaB100;
import com.centimo.api.domain.models.B100Balance;
import com.centimo.api.dto.B100BalanceRequest;
import com.centimo.api.dto.B100BalanceResponse;
import com.centimo.api.mappers.B100BalanceApiMapper;
import com.centimo.api.ports.driving.B100BalanceDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class B100BalanceController implements B100BalanceApi {

  private final B100BalanceDrivingPort b100BalanceDrivingPort;
  private final B100BalanceApiMapper mapper;

  @Override
  public ResponseEntity<List<B100BalanceResponse>> listB100Balances(String tipoSubcuenta, String mes, Integer limit, String order) {
    log.info("listB100Balances tipoSubcuenta={} mes={} limit={} order={}", tipoSubcuenta, mes, limit, order);
    TipoSubcuentaB100 tipo = parseTipoSubcuenta(tipoSubcuenta);
    if (tipo == null) {
      return ResponseEntity.badRequest().build();
    }
    List<B100BalanceResponse> balances = b100BalanceDrivingPort.listarPorSubcuenta(tipo, mes, limit, order).stream()
        .map(mapper::toB100BalanceResponse)
        .toList();
    return ResponseEntity.ok(balances);
  }

  @Override
  public ResponseEntity<B100BalanceResponse> createB100Balance(B100BalanceRequest b100BalanceRequest) {
    log.info("createB100Balance");
    B100Balance modeloEntrada = mapper.toDomain(b100BalanceRequest);
    B100Balance modeloCreado = b100BalanceDrivingPort.crear(modeloEntrada);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toB100BalanceResponse(modeloCreado));
  }

  @Override
  public ResponseEntity<B100BalanceResponse> updateB100Balance(String id, B100BalanceRequest b100BalanceRequest) {
    log.info("updateB100Balance id={}", id);
    B100Balance modeloEntrada = mapper.toDomain(b100BalanceRequest);
    B100Balance modeloActualizado = b100BalanceDrivingPort.actualizar(id, modeloEntrada);
    return ResponseEntity.ok(mapper.toB100BalanceResponse(modeloActualizado));
  }

  @Override
  public ResponseEntity<Void> deleteB100Balance(String id) {
    log.info("deleteB100Balance id={}", id);
    b100BalanceDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }

  private TipoSubcuentaB100 parseTipoSubcuenta(String tipoSubcuenta) {
    if (tipoSubcuenta == null) {
      return null;
    }
    try {
      return TipoSubcuentaB100.valueOf(tipoSubcuenta);
    } catch (IllegalArgumentException e) {
      log.warn("Tipo de subcuenta B100 inválido: {}", tipoSubcuenta);
      return null;
    }
  }
}