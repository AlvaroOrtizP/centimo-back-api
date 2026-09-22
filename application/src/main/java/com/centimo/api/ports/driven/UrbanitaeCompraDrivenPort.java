package com.centimo.api.ports.driven;

import com.centimo.api.domain.enums.EstadoUrbanitaeCompra;
import com.centimo.api.domain.models.UrbanitaeCompra;

import java.util.List;
import java.util.Optional;

public interface UrbanitaeCompraDrivenPort {

  Optional<UrbanitaeCompra> findById(String id);

  List<UrbanitaeCompra> findAll(String order);

  List<UrbanitaeCompra> findByEstado(EstadoUrbanitaeCompra estado, String order);

  UrbanitaeCompra guardar(UrbanitaeCompra compra);

  void eliminar(String id);
}