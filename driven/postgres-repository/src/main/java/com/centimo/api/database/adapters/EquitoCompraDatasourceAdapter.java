package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.EquitoCompraDatasourceMapper;
import com.centimo.api.database.models.EquitoCompraMO;
import com.centimo.api.database.repositories.EquitoCompraRepository;
import com.centimo.api.domain.enums.EstadoEquitoCompra;
import com.centimo.api.domain.models.EquitoCompra;
import com.centimo.api.ports.driven.EquitoCompraDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EquitoCompraDatasourceAdapter implements EquitoCompraDrivenPort {

  private final EquitoCompraRepository equitoCompraRepository;
  private final EquitoCompraDatasourceMapper mapper;

  @Override
  public Optional<EquitoCompra> findById(String id) {
    return equitoCompraRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<EquitoCompra> findAll(String order) {
    return equitoCompraRepository.findAll(sortPorFecha(order)).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<EquitoCompra> findByEstado(EstadoEquitoCompra estado, String order) {
    return equitoCompraRepository.findByEstado(estado, sortPorFecha(order)).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public EquitoCompra guardar(EquitoCompra compra) {
    EquitoCompraMO entity = compra.getId() != null
        ? equitoCompraRepository.findById(compra.getId()).orElse(mapper.toEntity(compra))
        : mapper.toEntity(compra);

    if (entity.getId() == null) {
      entity.setId(UUID.randomUUID().toString());
    }

    entity.setFecha(compra.getFecha());
    entity.setEntidad(compra.getEntidad());
    entity.setMonto(compra.getMonto());
    entity.setRendimiento(compra.getRendimiento());
    entity.setEstado(compra.getEstado());

    return mapper.toDomain(equitoCompraRepository.save(entity));
  }

  @Override
  public void eliminar(String id) {
    equitoCompraRepository.deleteById(id);
  }

  private Sort sortPorFecha(String order) {
    Sort.Direction direction = "asc".equalsIgnoreCase(order)
        ? Sort.Direction.ASC
        : Sort.Direction.DESC;
    return Sort.by(direction, "fecha");
  }
}