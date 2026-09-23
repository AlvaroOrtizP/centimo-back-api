package com.centimo.api.usecases;

import com.centimo.api.domain.models.Gasto;
import com.centimo.api.ports.driven.GastoDrivenPort;
import com.centimo.api.ports.driving.GastoDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GastoUseCase implements GastoDrivingPort {

  private final GastoDrivenPort gastoDrivenPort;

  @Override
  public List<Gasto> listar(Integer year, Integer month, String order) {
    if (year != null || month != null) {
      return gastoDrivenPort.findByPeriodo(year, month, order);
    }
    return gastoDrivenPort.findAll(order);
  }

  @Transactional
  @Override
  public Gasto crear(Gasto gasto) {
    return gastoDrivenPort.guardar(gasto);
  }

  @Transactional
  @Override
  public Gasto actualizar(String id, Gasto gasto) {
    Gasto existente = gastoDrivenPort.findById(id).orElseThrow();
    existente.setCategoria(gasto.getCategoria() != null ? gasto.getCategoria() : existente.getCategoria());
    existente.setCantidad(gasto.getCantidad() != null ? gasto.getCantidad() : existente.getCantidad());
    existente.setFecha(gasto.getFecha() != null ? gasto.getFecha() : existente.getFecha());
    existente.setDescripcion(gasto.getDescripcion());
    return gastoDrivenPort.guardar(existente);
  }

  @Transactional
  @Override
  public void eliminar(String id) {
    gastoDrivenPort.eliminar(id);
  }
}