package com.centimo.api.usecases;

import com.centimo.api.domain.enums.TipoSubcuentaB100;
import com.centimo.api.domain.models.B100Balance;
import com.centimo.api.ports.driven.B100BalanceDrivenPort;
import com.centimo.api.ports.driving.B100BalanceDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class B100BalanceUseCase implements B100BalanceDrivingPort {

  private final B100BalanceDrivenPort b100BalanceDrivenPort;

  @Override
  public Optional<B100Balance> obtenerPorSubcuentaYMes(TipoSubcuentaB100 tipoSubcuenta, String mes) {
    if (tipoSubcuenta == null || mes == null) {
      return Optional.empty();
    }
    return b100BalanceDrivenPort.findByTipoSubcuentaAndMes(tipoSubcuenta, mes);
  }

  @Override
  public List<B100Balance> listarPorSubcuenta(TipoSubcuentaB100 tipoSubcuenta, String mes, Integer limit, String order) {
    return b100BalanceDrivenPort.findByTipoSubcuenta(tipoSubcuenta, mes, limit, order);
  }

  @Transactional
  @Override
  public B100Balance crear(B100Balance balance) {
    aplicarDefaults(balance);
    Optional<B100Balance> existente = b100BalanceDrivenPort.findByTipoSubcuentaAndMes(
        balance.getTipoSubcuenta(), balance.getMes());
    if (existente.isPresent()) {
      B100Balance actualizado = actualizar(existente.get().getId(), balance);
      actualizado.setFechaCreacion(existente.get().getFechaCreacion());
      return actualizado;
    }
    balance.setId(idNatural(balance.getTipoSubcuenta(), balance.getMes()));
    return b100BalanceDrivenPort.guardar(balance);
  }

  @Transactional
  @Override
  public B100Balance actualizar(String id, B100Balance balance) {
    B100Balance existente = b100BalanceDrivenPort.findById(id).orElseThrow();
    existente.setTipoSubcuenta(balance.getTipoSubcuenta() != null ? balance.getTipoSubcuenta() : existente.getTipoSubcuenta());
    existente.setMes(balance.getMes() != null ? balance.getMes() : existente.getMes());
    existente.setBalanceMensual(balance.getBalanceMensual() != null ? balance.getBalanceMensual() : existente.getBalanceMensual());
    existente.setAporteMensual(balance.getAporteMensual() != null ? balance.getAporteMensual() : existente.getAporteMensual());
    existente.setDineroHacienda(balance.getDineroHacienda() != null ? balance.getDineroHacienda() : existente.getDineroHacienda());
    existente.setDineroTotalRepartir(balance.getDineroTotalRepartir() != null ? balance.getDineroTotalRepartir() : existente.getDineroTotalRepartir());
    existente.setPorcentajeHacienda(balance.getPorcentajeHacienda() != null ? balance.getPorcentajeHacienda() : existente.getPorcentajeHacienda());
    existente.setFechaActualizacion(LocalDateTime.now());
    return b100BalanceDrivenPort.guardar(existente);
  }

  @Transactional
  @Override
  public void eliminar(String id) {
    b100BalanceDrivenPort.eliminar(id);
  }

  private String idNatural(TipoSubcuentaB100 tipoSubcuenta, String mes) {
    return tipoSubcuenta.name() + "-" + mes;
  }

  private void aplicarDefaults(B100Balance balance) {
    if (balance.getAporteMensual() == null) {
      balance.setAporteMensual(BigDecimal.ZERO);
    }
    if (balance.getDineroHacienda() == null) {
      balance.setDineroHacienda(BigDecimal.ZERO);
    }
    if (balance.getPorcentajeHacienda() == null) {
      balance.setPorcentajeHacienda(new BigDecimal("19.00"));
    }
  }
}