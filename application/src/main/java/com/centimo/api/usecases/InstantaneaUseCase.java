package com.centimo.api.usecases;

import com.centimo.api.domain.exceptions.NotFoundException;
import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.ports.driven.InstantaneaDrivenPort;
import com.centimo.api.ports.driving.InstantaneaDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstantaneaUseCase implements InstantaneaDrivingPort {

  private final InstantaneaDrivenPort instantaneaDrivenPort;

  @Override
  @Transactional(readOnly = true)
  public List<InstantaneaMensual> buscarTodas(Integer anio, Integer mes, String cuentaId) {
    if (anio != null && mes != null && cuentaId != null) {
      return instantaneaDrivenPort.findByAnioMesAndCuentaId(anio, mes, cuentaId);
    }
    if (cuentaId != null) {
      return instantaneaDrivenPort.findByCuentaId(cuentaId);
    }
    if (anio != null && mes != null) {
      return instantaneaDrivenPort.findByAnioAndMes(anio, mes);
    }
    return instantaneaDrivenPort.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public InstantaneaMensual buscarPorId(String id) {
    return instantaneaDrivenPort.findById(id)
      .orElseThrow(() -> new NotFoundException("InstantaneaMensual", id));
  }

  @Override
  @Transactional
  public InstantaneaMensual crear(InstantaneaMensual instantanea) {
    return instantaneaDrivenPort.save(instantanea);
  }

  @Override
  @Transactional
  public InstantaneaMensual upsert(String cuentaId, Integer anio, Integer mes, BigDecimal saldo, BigDecimal deltaIngresos, BigDecimal gastos) {
    return instantaneaDrivenPort.findByCuentaIdAndAnioAndMes(cuentaId, anio, mes)
      .map(existente -> {
        existente.setSaldo(saldo);
        if (deltaIngresos != null && deltaIngresos.compareTo(BigDecimal.ZERO) != 0) {
          existente.setIngresos(existente.getIngresos().add(deltaIngresos));
        }
        if (gastos != null) {
          existente.setGastos(gastos);
        }
        return instantaneaDrivenPort.save(existente);
      })
      .orElseGet(() -> {
        InstantaneaMensual nueva = InstantaneaMensual.builder()
          .id(cuentaId + "-" + anio + "-" + String.format("%02d", mes))
          .cuentaId(cuentaId)
          .anio(anio)
          .mes(mes)
          .saldo(saldo)
          .ingresos(deltaIngresos != null && deltaIngresos.compareTo(BigDecimal.ZERO) > 0 ? deltaIngresos : BigDecimal.ZERO)
          .gastos(gastos != null ? gastos : BigDecimal.ZERO)
          .build();
        return instantaneaDrivenPort.save(nueva);
      });
  }

  @Override
  @Transactional
  public InstantaneaMensual actualizar(String id, InstantaneaMensual actualizada) {
    InstantaneaMensual existente = buscarPorId(id);
    existente.setSaldo(actualizada.getSaldo());
    existente.setIngresos(actualizada.getIngresos());
    existente.setGastos(actualizada.getGastos());
    existente.setAportacion(actualizada.getAportacion());
    existente.setNotas(actualizada.getNotas());
    return instantaneaDrivenPort.save(existente);
  }

  @Override
  @Transactional
  public void eliminar(String id) {
    instantaneaDrivenPort.deleteById(id);
  }
}
