package com.centimo.api.usecases;

import com.centimo.api.domain.enums.EstadoEquitoCompra;
import com.centimo.api.domain.models.EquitoCompra;
import com.centimo.api.ports.driven.EquitoCompraDrivenPort;
import com.centimo.api.ports.driving.EquitoCompraDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquitoCompraUseCase implements EquitoCompraDrivingPort {

  private final EquitoCompraDrivenPort equitoCompraDrivenPort;

  @Override
  public List<EquitoCompra> listar(EstadoEquitoCompra estado, String order) {
    return estado != null
        ? equitoCompraDrivenPort.findByEstado(estado, order)
        : equitoCompraDrivenPort.findAll(order);
  }

  @Transactional
  @Override
  public EquitoCompra crear(EquitoCompra compra) {
    aplicarDefaults(compra);
    return equitoCompraDrivenPort.guardar(compra);
  }

  @Transactional
  @Override
  public EquitoCompra actualizar(String id, EquitoCompra compra) {
    EquitoCompra existente = equitoCompraDrivenPort.findById(id).orElseThrow();
    existente.setFecha(compra.getFecha() != null ? compra.getFecha() : existente.getFecha());
    existente.setEntidad(compra.getEntidad() != null ? compra.getEntidad() : existente.getEntidad());
    existente.setMonto(compra.getMonto() != null ? compra.getMonto() : existente.getMonto());
    existente.setRendimiento(compra.getRendimiento() != null ? compra.getRendimiento() : existente.getRendimiento());
    existente.setEstado(compra.getEstado() != null ? compra.getEstado() : existente.getEstado());
    existente.setFechaActualizacion(LocalDateTime.now());
    return equitoCompraDrivenPort.guardar(existente);
  }

  @Transactional
  @Override
  public void eliminar(String id) {
    equitoCompraDrivenPort.eliminar(id);
  }

  private void aplicarDefaults(EquitoCompra compra) {
    if (compra.getRendimiento() == null) {
      compra.setRendimiento(BigDecimal.ZERO);
    }
    if (compra.getEstado() == null) {
      compra.setEstado(EstadoEquitoCompra.activa);
    }
  }
}