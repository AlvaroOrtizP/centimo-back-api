package com.centimo.api.usecases;

import com.centimo.api.domain.exceptions.NotFoundException;
import com.centimo.api.domain.models.OperacionInversion;
import com.centimo.api.ports.driven.OperacionInversionDrivenPort;
import com.centimo.api.ports.driving.OperacionInversionDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperacionInversionUseCase implements OperacionInversionDrivingPort {

  private final OperacionInversionDrivenPort operacionDrivenPort;

  @Override
  @Transactional(readOnly = true)
  public List<OperacionInversion> buscarTodas(String cuentaId) {
    return operacionDrivenPort.findByCuentaId(cuentaId);
  }

  @Override
  @Transactional(readOnly = true)
  public OperacionInversion buscarPorId(String id) {
    return operacionDrivenPort.findById(id)
      .orElseThrow(() -> new NotFoundException("OperacionInversion", id));
  }

  @Override
  @Transactional
  public OperacionInversion crear(OperacionInversion operacion) {
    return operacionDrivenPort.save(operacion);
  }

  @Override
  @Transactional
  public void eliminar(String id) {
    buscarPorId(id);
    operacionDrivenPort.deleteById(id);
  }
}
