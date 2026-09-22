package com.centimo.api.usecases;

import com.centimo.api.domain.models.UrbanitaeBalance;
import com.centimo.api.ports.driven.UrbanitaeBalanceDrivenPort;
import com.centimo.api.ports.driving.UrbanitaeBalanceDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrbanitaeBalanceUseCase implements UrbanitaeBalanceDrivingPort {

  private final UrbanitaeBalanceDrivenPort urbanitaeBalanceDrivenPort;

  @Override
  public List<UrbanitaeBalance> listar(Integer limit, String order) {
    return urbanitaeBalanceDrivenPort.findAll(limit, order);
  }

  @Transactional
  @Override
  public UrbanitaeBalance crear(UrbanitaeBalance balance) {
    aplicarDefaults(balance);
    Optional<UrbanitaeBalance> existente = urbanitaeBalanceDrivenPort.findByMes(balance.getMes());
    if (existente.isPresent()) {
      UrbanitaeBalance actualizado = actualizar(existente.get().getId(), balance);
      actualizado.setFechaCreacion(existente.get().getFechaCreacion());
      return actualizado;
    }
    balance.setId(balance.getMes());
    return urbanitaeBalanceDrivenPort.guardar(balance);
  }

  @Transactional
  @Override
  public UrbanitaeBalance actualizar(String id, UrbanitaeBalance balance) {
    UrbanitaeBalance existente = urbanitaeBalanceDrivenPort.findById(id).orElseThrow();
    existente.setMes(balance.getMes() != null ? balance.getMes() : existente.getMes());
    existente.setBalanceMensual(balance.getBalanceMensual() != null ? balance.getBalanceMensual() : existente.getBalanceMensual());
    existente.setAporteMensual(balance.getAporteMensual() != null ? balance.getAporteMensual() : existente.getAporteMensual());
    existente.setDineroTotal(balance.getDineroTotal() != null ? balance.getDineroTotal() : existente.getDineroTotal());
    existente.setDineroHacienda(balance.getDineroHacienda() != null ? balance.getDineroHacienda() : existente.getDineroHacienda());
    existente.setDineroFinal(balance.getDineroFinal() != null ? balance.getDineroFinal() : existente.getDineroFinal());
    existente.setFechaActualizacion(LocalDateTime.now());
    return urbanitaeBalanceDrivenPort.guardar(existente);
  }

  @Transactional
  @Override
  public void eliminar(String id) {
    urbanitaeBalanceDrivenPort.eliminar(id);
  }

  private void aplicarDefaults(UrbanitaeBalance balance) {
    if (balance.getAporteMensual() == null) {
      balance.setAporteMensual(BigDecimal.ZERO);
    }
    if (balance.getDineroTotal() == null) {
      balance.setDineroTotal(BigDecimal.ZERO);
    }
    if (balance.getDineroHacienda() == null) {
      balance.setDineroHacienda(BigDecimal.ZERO);
    }
    if (balance.getDineroFinal() == null) {
      balance.setDineroFinal(BigDecimal.ZERO);
    }
  }
}