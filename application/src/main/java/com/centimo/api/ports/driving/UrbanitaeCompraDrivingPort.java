package com.centimo.api.ports.driving;

import com.centimo.api.domain.enums.EstadoUrbanitaeCompra;
import com.centimo.api.domain.models.UrbanitaeCompra;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UrbanitaeCompraDrivingPort {

  List<UrbanitaeCompra> listar(EstadoUrbanitaeCompra estado, String order);

  @Transactional
  UrbanitaeCompra crear(UrbanitaeCompra compra);

  @Transactional
  UrbanitaeCompra actualizar(String id, UrbanitaeCompra compra);

  @Transactional
  void eliminar(String id);
}