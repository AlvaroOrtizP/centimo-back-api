package com.centimo.api.usecases;

import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.ports.driven.InstantaneaDrivenPort;
import com.centimo.api.ports.driving.InstantaneaDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstantaneaUseCase implements InstantaneaDrivingPort {

  private final InstantaneaDrivenPort instantaneaDrivenPort;

  @Override
  public Optional<InstantaneaMensual> obtenerPorFecha(String accountId, Integer year, Integer month) {
    return instantaneaDrivenPort.findByAnioAndMes(accountId, year, month);
  }

  @Transactional
  @Override
  public InstantaneaMensual crear(InstantaneaMensual nuevaInstantanea) {
    // Aquí puedes agregar validaciones de negocio adicionales
    // Ej. verificar que no exista ya una instantánea para la misma cuenta en el mismo mes/año

    return instantaneaDrivenPort.guardar(nuevaInstantanea);
  }
}
