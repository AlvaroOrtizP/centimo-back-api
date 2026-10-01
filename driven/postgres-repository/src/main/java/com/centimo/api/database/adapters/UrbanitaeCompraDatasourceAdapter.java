package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.UrbanitaeCompraDatasourceMapper;
import com.centimo.api.database.models.UrbanitaeCompraMO;
import com.centimo.api.database.repositories.UrbanitaeCompraRepository;
import com.centimo.api.domain.enums.EstadoUrbanitaeCompra;
import com.centimo.api.domain.models.UrbanitaeCompra;
import com.centimo.api.ports.driven.UrbanitaeCompraDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrbanitaeCompraDatasourceAdapter implements UrbanitaeCompraDrivenPort {

  private final UrbanitaeCompraRepository urbanitaeCompraRepository;
  private final UrbanitaeCompraDatasourceMapper mapper;

  @Override
  public Optional<UrbanitaeCompra> findById(String id) {
    return urbanitaeCompraRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<UrbanitaeCompra> findAll(String order) {
    return urbanitaeCompraRepository.findAll(sortPorFecha(order)).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<UrbanitaeCompra> findByEstado(EstadoUrbanitaeCompra estado, String order) {
    return urbanitaeCompraRepository.findByEstado(estado, sortPorFecha(order)).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public UrbanitaeCompra guardar(UrbanitaeCompra compra) {
    UrbanitaeCompraMO entity = compra.getId() != null
        ? urbanitaeCompraRepository.findById(compra.getId()).orElse(mapper.toEntity(compra))
        : mapper.toEntity(compra);

    if (entity.getId() == null) {
      entity.setId(UUID.randomUUID().toString());
    }

    entity.setFecha(compra.getFecha());
    entity.setEntidad(compra.getEntidad());
    entity.setMonto(compra.getMonto());
    entity.setRendimiento(compra.getRendimiento());
    entity.setEstado(compra.getEstado());

    return mapper.toDomain(urbanitaeCompraRepository.save(entity));
  }

  @Override
  public void eliminar(String id) {
    urbanitaeCompraRepository.deleteById(id);
  }

  private Sort sortPorFecha(String order) {
    Sort.Direction direction = "asc".equalsIgnoreCase(order)
        ? Sort.Direction.ASC
        : Sort.Direction.DESC;
    return Sort.by(direction, "fecha");
  }
}