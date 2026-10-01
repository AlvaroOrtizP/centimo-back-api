package com.centimo.api.usecases;

import com.centimo.api.domain.models.RevolutBalance;
import com.centimo.api.ports.driven.RevolutBalanceDrivenPort;
import com.centimo.api.ports.driving.RevolutBalanceDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RevolutBalanceUseCase implements RevolutBalanceDrivingPort {

  private final RevolutBalanceDrivenPort revolutBalanceDrivenPort;

  @Override
  public List<RevolutBalance> listar(Integer limit, String order) {
    return revolutBalanceDrivenPort.findAll(limit, order);
  }

  @Transactional
  @Override
  public RevolutBalance crear(RevolutBalance balance) {
    aplicarDefaults(balance);
    Optional<RevolutBalance> existente = revolutBalanceDrivenPort.findByMes(balance.getMes());
    if (existente.isPresent()) {
      RevolutBalance actualizado = actualizar(existente.get().getId(), balance);
      actualizado.setFechaCreacion(existente.get().getFechaCreacion());
      return actualizado;
    }
    balance.setId(balance.getMes());
    return revolutBalanceDrivenPort.guardar(balance);
  }

  @Transactional
  @Override
  public RevolutBalance actualizar(String id, RevolutBalance balance) {
    RevolutBalance existente = revolutBalanceDrivenPort.findById(id).orElseThrow();
    existente.setMes(balance.getMes() != null ? balance.getMes() : existente.getMes());
    existente.setBalanceMensual(balance.getBalanceMensual() != null ? balance.getBalanceMensual() : existente.getBalanceMensual());
    existente.setAporteMensual(balance.getAporteMensual() != null ? balance.getAporteMensual() : existente.getAporteMensual());
    existente.setDineroTotal(balance.getDineroTotal() != null ? balance.getDineroTotal() : existente.getDineroTotal());
    existente.setDineroHacienda(balance.getDineroHacienda() != null ? balance.getDineroHacienda() : existente.getDineroHacienda());
    existente.setDineroFinal(balance.getDineroFinal() != null ? balance.getDineroFinal() : existente.getDineroFinal());
    existente.setFechaActualizacion(LocalDateTime.now());
    return revolutBalanceDrivenPort.guardar(existente);
  }

  @Transactional
  @Override
  public void eliminar(String id) {
    revolutBalanceDrivenPort.eliminar(id);
  }

  private void aplicarDefaults(RevolutBalance balance) {
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
