package com.centimo.api.usecases;

import com.centimo.api.domain.models.BancoBalance;
import com.centimo.api.ports.driven.BancoBalanceDrivenPort;
import com.centimo.api.ports.driving.BancoBalanceDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BancoBalanceUseCase implements BancoBalanceDrivingPort {

  private final BancoBalanceDrivenPort bancoBalanceDrivenPort;

  @Override
  public List<BancoBalance> listarPorEntidad(String entidad, Integer limit, String order) {
    return bancoBalanceDrivenPort.findByEntidad(entidad, limit, order);
  }

  @Transactional
  @Override
  public BancoBalance crear(BancoBalance balance) {
    aplicarDefaults(balance);
    Optional<BancoBalance> existente = bancoBalanceDrivenPort.findByEntidadAndMes(
        balance.getEntidad(), balance.getMes());
    if (existente.isPresent()) {
      BancoBalance actualizado = actualizar(existente.get().getId(), balance);
      actualizado.setFechaCreacion(existente.get().getFechaCreacion());
      return actualizado;
    }
    balance.setId(idNatural(balance.getEntidad(), balance.getMes()));
    return bancoBalanceDrivenPort.guardar(balance);
  }

  @Transactional
  @Override
  public BancoBalance actualizar(String id, BancoBalance balance) {
    BancoBalance existente = bancoBalanceDrivenPort.findById(id).orElseThrow();
    existente.setBalanceMensual(balance.getBalanceMensual() != null ? balance.getBalanceMensual() : existente.getBalanceMensual());
    existente.setAporteMensual(balance.getAporteMensual() != null ? balance.getAporteMensual() : existente.getAporteMensual());
    existente.setFechaActualizacion(LocalDateTime.now());
    return bancoBalanceDrivenPort.guardar(existente);
  }

  @Transactional
  @Override
  public void eliminar(String id) {
    bancoBalanceDrivenPort.eliminar(id);
  }

  private String idNatural(String entidad, String mes) {
    return entidad + "-" + mes;
  }

  private void aplicarDefaults(BancoBalance balance) {
    if (balance.getAporteMensual() == null) {
      balance.setAporteMensual(BigDecimal.ZERO);
    }
  }
}