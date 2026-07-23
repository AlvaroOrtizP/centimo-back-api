package com.centimo.api.usecases;

import com.centimo.api.domain.models.Nomina;
import com.centimo.api.ports.driven.NominaDrivenPort;
import com.centimo.api.ports.driving.NominaDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NominaUseCase implements NominaDrivingPort {

  private final NominaDrivenPort nominaDrivenPort;

  @Override
  public Optional<Nomina> obtenerPorFecha(Integer year, Integer month) {
    return nominaDrivenPort.findByAnioAndMes(year, month);
  }

  @Transactional
  @Override
  public Nomina crear(Nomina nuevaNomina) {
    return nominaDrivenPort.guardar(nuevaNomina);
  }
}
