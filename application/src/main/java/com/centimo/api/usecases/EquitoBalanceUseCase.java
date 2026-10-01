package com.centimo.api.usecases;

import com.centimo.api.domain.models.EquitoBalance;
import com.centimo.api.ports.driven.EquitoBalanceDrivenPort;
import com.centimo.api.ports.driving.EquitoBalanceDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquitoBalanceUseCase implements EquitoBalanceDrivingPort {

  private final EquitoBalanceDrivenPort equitoBalanceDrivenPort;

  @Override
  public List<EquitoBalance> listar(Integer limit, String order) {
    return equitoBalanceDrivenPort.findAll(limit, order);
  }

  @Transactional
  @Override
  public EquitoBalance crear(EquitoBalance balance) {
    aplicarDefaults(balance);
    Optional<EquitoBalance> existente = equitoBalanceDrivenPort.findByMes(balance.getMes());
    if (existente.isPresent()) {
      EquitoBalance actualizado = actualizar(existente.get().getId(), balance);
      actualizado.setFechaCreacion(existente.get().getFechaCreacion());
      return actualizado;
    }
    balance.setId(balance.getMes());
    return equitoBalanceDrivenPort.guardar(balance);
  }

  @Transactional
  @Override
  public EquitoBalance actualizar(String id, EquitoBalance balance) {
    EquitoBalance existente = equitoBalanceDrivenPort.findById(id).orElseThrow();
    existente.setMes(balance.getMes() != null ? balance.getMes() : existente.getMes());
    existente.setBalanceMensual(balance.getBalanceMensual() != null ? balance.getBalanceMensual() : existente.getBalanceMensual());
    existente.setAporteMensual(balance.getAporteMensual() != null ? balance.getAporteMensual() : existente.getAporteMensual());
    existente.setDineroTotal(balance.getDineroTotal() != null ? balance.getDineroTotal() : existente.getDineroTotal());
    existente.setDineroHacienda(balance.getDineroHacienda() != null ? balance.getDineroHacienda() : existente.getDineroHacienda());
    existente.setDineroFinal(balance.getDineroFinal() != null ? balance.getDineroFinal() : existente.getDineroFinal());
    existente.setFechaActualizacion(LocalDateTime.now());
    return equitoBalanceDrivenPort.guardar(existente);
  }

  @Transactional
  @Override
  public void eliminar(String id) {
    equitoBalanceDrivenPort.eliminar(id);
  }

  private void aplicarDefaults(EquitoBalance balance) {
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