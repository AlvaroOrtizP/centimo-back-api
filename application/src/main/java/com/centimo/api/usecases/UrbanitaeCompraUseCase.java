package com.centimo.api.usecases;

import com.centimo.api.domain.enums.EstadoUrbanitaeCompra;
import com.centimo.api.domain.models.UrbanitaeCompra;
import com.centimo.api.ports.driven.UrbanitaeCompraDrivenPort;
import com.centimo.api.ports.driving.UrbanitaeCompraDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrbanitaeCompraUseCase implements UrbanitaeCompraDrivingPort {

  private final UrbanitaeCompraDrivenPort urbanitaeCompraDrivenPort;

  @Override
  public List<UrbanitaeCompra> listar(EstadoUrbanitaeCompra estado, String order) {
    return estado != null
        ? urbanitaeCompraDrivenPort.findByEstado(estado, order)
        : urbanitaeCompraDrivenPort.findAll(order);
  }

  @Transactional
  @Override
  public UrbanitaeCompra crear(UrbanitaeCompra compra) {
    aplicarDefaults(compra);
    return urbanitaeCompraDrivenPort.guardar(compra);
  }

  @Transactional
  @Override
  public UrbanitaeCompra actualizar(String id, UrbanitaeCompra compra) {
    UrbanitaeCompra existente = urbanitaeCompraDrivenPort.findById(id).orElseThrow();
    existente.setFecha(compra.getFecha() != null ? compra.getFecha() : existente.getFecha());
    existente.setEntidad(compra.getEntidad() != null ? compra.getEntidad() : existente.getEntidad());
    existente.setMonto(compra.getMonto() != null ? compra.getMonto() : existente.getMonto());
    existente.setRendimiento(compra.getRendimiento() != null ? compra.getRendimiento() : existente.getRendimiento());
    existente.setEstado(compra.getEstado() != null ? compra.getEstado() : existente.getEstado());
    existente.setFechaActualizacion(LocalDateTime.now());
    return urbanitaeCompraDrivenPort.guardar(existente);
  }

  @Transactional
  @Override
  public void eliminar(String id) {
    urbanitaeCompraDrivenPort.eliminar(id);
  }

  private void aplicarDefaults(UrbanitaeCompra compra) {
    if (compra.getRendimiento() == null) {
      compra.setRendimiento(BigDecimal.ZERO);
    }
    if (compra.getEstado() == null) {
      compra.setEstado(EstadoUrbanitaeCompra.activa);
    }
  }
}