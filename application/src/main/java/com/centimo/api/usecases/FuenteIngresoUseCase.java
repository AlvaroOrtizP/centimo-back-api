package com.centimo.api.usecases;

import com.centimo.api.domain.exceptions.NotFoundException;
import com.centimo.api.domain.models.FuenteIngreso;
import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.ports.driven.FuenteIngresoDrivenPort;
import com.centimo.api.ports.driven.InstantaneaDrivenPort;
import com.centimo.api.ports.driving.FuenteIngresoDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuenteIngresoUseCase implements FuenteIngresoDrivingPort {

  private final FuenteIngresoDrivenPort fuenteIngresoDrivenPort;
  private final InstantaneaDrivenPort instantaneaDrivenPort;

  @Override
  @Transactional(readOnly = true)
  public List<FuenteIngreso> buscarTodas(String instantaneaId) {
    return fuenteIngresoDrivenPort.findByInstantaneaId(instantaneaId);
  }

  @Override
  @Transactional(readOnly = true)
  public FuenteIngreso buscarPorId(String id) {
    return fuenteIngresoDrivenPort.findById(id)
      .orElseThrow(() -> new NotFoundException("FuenteIngreso", id));
  }

  @Override
  @Transactional
  public FuenteIngreso crear(FuenteIngreso ingreso) {
    FuenteIngreso saved = fuenteIngresoDrivenPort.save(ingreso);
    InstantaneaMensual instantanea = instantaneaDrivenPort.findById(ingreso.getInstantaneaId())
      .orElseThrow(() -> new NotFoundException("InstantaneaMensual", ingreso.getInstantaneaId()));
    instantanea.setIngresos(instantanea.getIngresos().add(ingreso.getCantidad()));
    instantaneaDrivenPort.save(instantanea);
    return saved;
  }

  @Override
  @Transactional
  public void eliminar(String id) {
    FuenteIngreso ingreso = buscarPorId(id);
    fuenteIngresoDrivenPort.deleteById(id);
    InstantaneaMensual instantanea = instantaneaDrivenPort.findById(ingreso.getInstantaneaId())
      .orElseThrow(() -> new NotFoundException("InstantaneaMensual", ingreso.getInstantaneaId()));
    instantanea.setIngresos(instantanea.getIngresos().subtract(ingreso.getCantidad()));
    instantaneaDrivenPort.save(instantanea);
  }
}
