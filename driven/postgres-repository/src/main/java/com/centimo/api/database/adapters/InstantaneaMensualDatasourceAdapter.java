package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.InstantaneaMensualMapper;
import com.centimo.api.database.models.InstantaneaMensualMO;
import com.centimo.api.database.repositories.CuentaRepository;
import com.centimo.api.database.repositories.InstantaneaMensualRepository;
import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.ports.driven.InstantaneaDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstantaneaMensualDatasourceAdapter implements InstantaneaDrivenPort {

  private final InstantaneaMensualRepository instantaneaRepository;
  private final CuentaRepository cuentaRepository;
  private final InstantaneaMensualMapper mapper;

  @Override
  public List<InstantaneaMensual> findAll() {
    return instantaneaRepository.findAll().stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public List<InstantaneaMensual> findByAnioAndMes(Integer anio, Integer mes) {
    return instantaneaRepository.findByAnioAndMes(anio, mes).stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public List<InstantaneaMensual> findByCuentaId(String cuentaId) {
    return instantaneaRepository.findByCuentaId(cuentaId).stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public List<InstantaneaMensual> findByAnioMesAndCuentaId(Integer anio, Integer mes, String cuentaId) {
    return instantaneaRepository.findByAnioAndMesAndCuentaId(anio, mes, cuentaId).stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public Optional<InstantaneaMensual> findByCuentaIdAndAnioAndMes(String cuentaId, Integer anio, Integer mes) {
    return instantaneaRepository.findByCuentaIdAndAnioAndMes(cuentaId, anio, mes).map(mapper::toDomain);
  }

  @Override
  public Optional<InstantaneaMensual> findById(String id) {
    return instantaneaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public InstantaneaMensual save(InstantaneaMensual instantanea) {
    InstantaneaMensualMO mo = mapper.toMO(instantanea);
    if (instantanea.getCuentaId() != null && mo.getCuenta() == null) {
      mo.setCuenta(cuentaRepository.getReferenceById(instantanea.getCuentaId()));
    }
    InstantaneaMensualMO saved = instantaneaRepository.save(mo);
    return mapper.toDomain(saved);
  }

  @Override
  public void deleteById(String id) {
    instantaneaRepository.deleteById(id);
  }
}
