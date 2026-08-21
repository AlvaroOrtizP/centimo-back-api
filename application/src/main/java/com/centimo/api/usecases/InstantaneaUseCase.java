package com.centimo.api.usecases;

import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.ports.driven.InstantaneaDrivenPort;
import com.centimo.api.ports.driving.InstantaneaDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstantaneaUseCase implements InstantaneaDrivingPort {

  private final InstantaneaDrivenPort instantaneaDrivenPort;

  @Override
  public Optional<InstantaneaMensual> obtenerPorFecha(String accountId, Integer year, Integer month) {
    return instantaneaDrivenPort.findByAnioAndMes(accountId, year, month);
  }

  @Override
  public List<InstantaneaMensual> listarTodas(Integer year, String accountId) {
    if (accountId != null && year != null) {
      return instantaneaDrivenPort.findByCuentaIdAndAnio(accountId, year);
    }
    if (accountId != null) {
      return instantaneaDrivenPort.findByCuentaId(accountId);
    }
    if (year != null) {
      return instantaneaDrivenPort.findByAnio(year);
    }
    return instantaneaDrivenPort.findAll();
  }

  @Transactional
  @Override
  public InstantaneaMensual crear(InstantaneaMensual nuevaInstantanea) {
    return instantaneaDrivenPort.guardar(nuevaInstantanea);
  }

  @Transactional
  @Override
  public InstantaneaMensual upsert(String accountId, Integer year, Integer month,
                                   BigDecimal balance, BigDecimal incomeDelta, BigDecimal expenses,
                                   BigDecimal contribution, BigDecimal hacienda) {
    Optional<InstantaneaMensual> existente = instantaneaDrivenPort.findByAnioAndMes(accountId, year, month);

    if (existente.isPresent()) {
      InstantaneaMensual instantanea = existente.get();
      instantanea.setSaldo(balance);
      instantanea.setIngresos(incomeDelta);
      if (expenses != null) {
        instantanea.setGastos(expenses);
      }
      if (contribution != null) {
        instantanea.setAportacion(contribution);
      }
      instantanea.setHacienda(hacienda);
      return instantaneaDrivenPort.guardar(instantanea);
    }

    InstantaneaMensual nueva = InstantaneaMensual.builder()
        .cuentaId(accountId)
        .anio(year)
        .mes(month)
        .saldo(balance)
        .ingresos(incomeDelta)
        .gastos(expenses != null ? expenses : BigDecimal.ZERO)
        .aportacion(contribution)
        .hacienda(hacienda)
        .build();
    return instantaneaDrivenPort.guardar(nueva);
  }

  @Transactional
  @Override
  public Optional<InstantaneaMensual> actualizar(String id, InstantaneaMensual cambios) {
    return instantaneaDrivenPort.findById(id).map(existente -> {
      existente.setSaldo(cambios.getSaldo());
      existente.setIngresos(cambios.getIngresos());
      existente.setGastos(cambios.getGastos());
      existente.setAportacion(cambios.getAportacion());
      existente.setHacienda(cambios.getHacienda());
      existente.setNotas(cambios.getNotas());
      return instantaneaDrivenPort.guardar(existente);
    });
  }

  @Transactional
  @Override
  public boolean eliminar(String id) {
    if (instantaneaDrivenPort.findById(id).isEmpty()) {
      return false;
    }
    instantaneaDrivenPort.eliminar(id);
    return true;
  }
}
