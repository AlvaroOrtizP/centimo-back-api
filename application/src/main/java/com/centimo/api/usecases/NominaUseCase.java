package com.centimo.api.usecases;

import com.centimo.api.domain.exception.InvalidRequestException;
import com.centimo.api.domain.exception.ResourceNotFoundException;
import com.centimo.api.domain.models.Nomina;
import com.centimo.api.ports.driven.NominaDrivenPort;
import com.centimo.api.ports.driving.NominaDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class NominaUseCase implements NominaDrivingPort {

  private static final Pattern MES_VALIDO = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])$");

  private final NominaDrivenPort nominaDrivenPort;

  @Override
  public Optional<Nomina> obtener(String mes) {
    validarMes(mes);
    return nominaDrivenPort.findByMes(mes);
  }

  @Transactional
  @Override
  public Nomina guardar(Nomina nomina) {
    validarMes(nomina.getMes());
    if (nomina.getCantidad() == null) {
      nomina.setCantidad(BigDecimal.ZERO);
    }
    Optional<Nomina> existente = nominaDrivenPort.findByMes(nomina.getMes());
    if (existente.isPresent()) {
      Nomina actualizada = existente.get();
      actualizada.setCantidad(nomina.getCantidad());
      actualizada.setNota(nomina.getNota());
      actualizada.setFechaActualizacion(LocalDateTime.now());
      return nominaDrivenPort.guardar(actualizada);
    }
    nomina.setFechaActualizacion(null);
    return nominaDrivenPort.guardar(nomina);
  }

  @Transactional
  @Override
  public Nomina actualizar(String mes, Nomina nomina) {
    validarMes(mes);
    Nomina existente = nominaDrivenPort.findByMes(mes)
        .orElseThrow(() -> new ResourceNotFoundException("No existe nómina para el mes " + mes));
    if (nomina.getCantidad() != null) {
      existente.setCantidad(nomina.getCantidad());
    }
    if (nomina.getNota() != null) {
      existente.setNota(nomina.getNota());
    }
    existente.setFechaActualizacion(LocalDateTime.now());
    return nominaDrivenPort.guardar(existente);
  }

  @Transactional
  @Override
  public void eliminar(String mes) {
    validarMes(mes);
    nominaDrivenPort.findByMes(mes)
        .orElseThrow(() -> new ResourceNotFoundException("No existe nómina para el mes " + mes));
    nominaDrivenPort.eliminar(mes);
  }

  private void validarMes(String mes) {
    if (mes == null || mes.isBlank()) {
      throw new InvalidRequestException("El mes es obligatorio");
    }
    if (!MES_VALIDO.matcher(mes).matches()) {
      throw new InvalidRequestException("El mes debe tener formato YYYY-MM");
    }
  }
}