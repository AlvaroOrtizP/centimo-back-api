package com.centimo.api.ports.driving;

import com.centimo.api.domain.enums.EstadoEquitoCompra;
import com.centimo.api.domain.models.EquitoCompra;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EquitoCompraDrivingPort {

  List<EquitoCompra> listar(EstadoEquitoCompra estado, String order);

  @Transactional
  EquitoCompra crear(EquitoCompra compra);

  @Transactional
  EquitoCompra actualizar(String id, EquitoCompra compra);

  @Transactional
  void eliminar(String id);
}