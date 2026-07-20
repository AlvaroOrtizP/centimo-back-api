package com.centimo.api.usecases;

import com.centimo.api.domain.exceptions.NotFoundException;
import com.centimo.api.domain.models.Gasto;
import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.ports.driven.GastoDrivenPort;
import com.centimo.api.ports.driven.InstantaneaDrivenPort;
import com.centimo.api.ports.driving.GastoDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GastoUseCase implements GastoDrivingPort {

  private final GastoDrivenPort gastoDrivenPort;
  private final InstantaneaDrivenPort instantaneaDrivenPort;

  @Override
  @Transactional(readOnly = true)
  public List<Gasto> buscarTodos(String instantaneaId) {
    return gastoDrivenPort.findByInstantaneaId(instantaneaId);
  }

  @Override
  @Transactional(readOnly = true)
  public Gasto buscarPorId(String id) {
    return gastoDrivenPort.findById(id)
      .orElseThrow(() -> new NotFoundException("Gasto", id));
  }

  @Override
  @Transactional
  public Gasto crear(Gasto gasto) {
    Gasto saved = gastoDrivenPort.save(gasto);
    InstantaneaMensual instantanea = instantaneaDrivenPort.findById(gasto.getInstantaneaId())
      .orElseThrow(() -> new NotFoundException("InstantaneaMensual", gasto.getInstantaneaId()));
    instantanea.setGastos(instantanea.getGastos().add(gasto.getCantidad()));
    instantaneaDrivenPort.save(instantanea);
    return saved;
  }

  @Override
  @Transactional
  public void eliminar(String id) {
    Gasto gasto = buscarPorId(id);
    gastoDrivenPort.deleteById(id);
    InstantaneaMensual instantanea = instantaneaDrivenPort.findById(gasto.getInstantaneaId())
      .orElseThrow(() -> new NotFoundException("InstantaneaMensual", gasto.getInstantaneaId()));
    instantanea.setGastos(instantanea.getGastos().subtract(gasto.getCantidad()));
    instantaneaDrivenPort.save(instantanea);
  }
}
