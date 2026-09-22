package com.centimo.api.ports.driven;

import com.centimo.api.domain.enums.EstadoEquitoCompra;
import com.centimo.api.domain.models.EquitoCompra;

import java.util.List;
import java.util.Optional;

public interface EquitoCompraDrivenPort {

  Optional<EquitoCompra> findById(String id);

  List<EquitoCompra> findAll(String order);

  List<EquitoCompra> findByEstado(EstadoEquitoCompra estado, String order);

  EquitoCompra guardar(EquitoCompra compra);

  void eliminar(String id);
}