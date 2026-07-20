package com.centimo.api.usecases;

import com.centimo.api.domain.exceptions.NotFoundException;
import com.centimo.api.domain.models.AsignacionSalario;
import com.centimo.api.ports.driven.AsignacionSalarioDrivenPort;
import com.centimo.api.ports.driving.AsignacionSalarioDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AsignacionSalarioUseCase implements AsignacionSalarioDrivingPort {

  private final AsignacionSalarioDrivenPort asignacionDrivenPort;

  @Override
  @Transactional(readOnly = true)
  public List<AsignacionSalario> buscarTodas(Integer anio, Integer mes) {
    if (anio != null && mes != null) {
      return asignacionDrivenPort.findByAnioAndMes(anio, mes);
    }
    return asignacionDrivenPort.findByAnioAndMes(null, null);
  }

  @Override
  @Transactional(readOnly = true)
  public AsignacionSalario buscarPorId(String id) {
    return asignacionDrivenPort.findById(id)
      .orElseThrow(() -> new NotFoundException("AsignacionSalario", id));
  }

  @Override
  @Transactional
  public AsignacionSalario crear(AsignacionSalario asignacion) {
    return asignacionDrivenPort.save(asignacion);
  }

  @Override
  @Transactional
  public AsignacionSalario actualizar(String id, AsignacionSalario asignacion) {
    buscarPorId(id);
    asignacion.setId(id);
    return asignacionDrivenPort.save(asignacion);
  }

  @Override
  @Transactional
  public void eliminar(String id) {
    buscarPorId(id);
    asignacionDrivenPort.deleteById(id);
  }
}
