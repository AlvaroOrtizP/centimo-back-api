package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.GastoDatasourceMapper;
import com.centimo.api.database.models.GastoMO;
import com.centimo.api.database.repositories.GastoRepository;
import com.centimo.api.domain.models.Gasto;
import com.centimo.api.ports.driven.GastoDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GastoDatasourceAdapter implements GastoDrivenPort {

  private final GastoRepository gastoRepository;
  private final GastoDatasourceMapper mapper;

  @Override
  public Optional<Gasto> findById(String id) {
    return gastoRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<Gasto> findByPeriodo(Integer year, Integer month, String order) {
    return gastoRepository.findByFechaBetween(inicioPeriodo(year, month), finPeriodo(year, month), sortPorFecha(order)).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<Gasto> findAll(String order) {
    return gastoRepository.findAll(sortPorFecha(order)).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public Gasto guardar(Gasto gasto) {
    GastoMO entity = gasto.getId() != null
        ? gastoRepository.findById(gasto.getId()).orElse(mapper.toEntity(gasto))
        : mapper.toEntity(gasto);

    if (entity.getId() == null) {
      entity.setId(UUID.randomUUID().toString());
    }

    entity.setCategoria(gasto.getCategoria());
    entity.setCantidad(gasto.getCantidad());
    entity.setFecha(gasto.getFecha());
    entity.setDescripcion(gasto.getDescripcion());

    return mapper.toDomain(gastoRepository.save(entity));
  }

  @Override
  public void eliminar(String id) {
    gastoRepository.deleteById(id);
  }

  private Sort sortPorFecha(String order) {
    Sort.Direction direction = "asc".equalsIgnoreCase(order)
        ? Sort.Direction.ASC
        : Sort.Direction.DESC;
    return Sort.by(direction, "fecha");
  }

  private LocalDate inicioPeriodo(Integer year, Integer month) {
    if (year == null || month == null) {
      return year == null ? LocalDate.MIN : LocalDate.of(year, 1, 1);
    }
    return LocalDate.of(year, month, 1);
  }

  private LocalDate finPeriodo(Integer year, Integer month) {
    if (year == null || month == null) {
      return year == null ? LocalDate.MAX : LocalDate.of(year, 12, 31);
    }
    return YearMonth.of(year, month).atEndOfMonth();
  }
}