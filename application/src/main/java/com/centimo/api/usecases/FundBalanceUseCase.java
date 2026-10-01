package com.centimo.api.usecases;

import com.centimo.api.domain.models.BalanceFondo;
import com.centimo.api.ports.driven.FundBalanceDrivenPort;
import com.centimo.api.ports.driving.FundBalanceDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FundBalanceUseCase implements FundBalanceDrivingPort {

  private final FundBalanceDrivenPort fundBalanceDrivenPort;

  @Override
  public List<BalanceFondo> listarPorMes(Integer anio, Integer mes) {
    return fundBalanceDrivenPort.findByAnioAndMes(anio, mes);
  }

  @Transactional
  @Override
  public BalanceFondo crear(BalanceFondo balance) {
    aplicarDefaults(balance);
    Optional<BalanceFondo> existente = fundBalanceDrivenPort.findByFondoIdAnioAndMes(
        balance.getFondoId(), balance.getAnio(), balance.getMes());
    if (existente.isPresent()) {
      return actualizar(existente.get().getId(), balance);
    }
    return fundBalanceDrivenPort.guardar(balance);
  }

  @Transactional
  @Override
  public BalanceFondo actualizar(String id, BalanceFondo balance) {
    BalanceFondo existente = fundBalanceDrivenPort.findById(id).orElseThrow();
    existente.setSaldo(balance.getSaldo() != null ? balance.getSaldo() : existente.getSaldo());
    existente.setIntereses(balance.getIntereses() != null ? balance.getIntereses() : existente.getIntereses());
    existente.setAportacion(balance.getAportacion() != null ? balance.getAportacion() : existente.getAportacion());
    existente.setRetirada(balance.getRetirada() != null ? balance.getRetirada() : existente.getRetirada());
    return fundBalanceDrivenPort.guardar(existente);
  }

  @Transactional
  @Override
  public void eliminar(String id) {
    fundBalanceDrivenPort.eliminar(id);
  }

  private void aplicarDefaults(BalanceFondo balance) {
    if (balance.getIntereses() == null) {
      balance.setIntereses(BigDecimal.ZERO);
    }
    if (balance.getAportacion() == null) {
      balance.setAportacion(BigDecimal.ZERO);
    }
    if (balance.getRetirada() == null) {
      balance.setRetirada(BigDecimal.ZERO);
    }
  }
}