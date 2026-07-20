package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.AsignacionSalarioMapper;
import com.centimo.api.database.models.AsignacionSalarioMO;
import com.centimo.api.database.models.PlataformaMO;
import com.centimo.api.database.repositories.AsignacionSalarioRepository;
import com.centimo.api.domain.models.AsignacionSalario;
import com.centimo.api.ports.driven.AsignacionSalarioDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AsignacionSalarioDatasourceAdapter implements AsignacionSalarioDrivenPort {

  private final AsignacionSalarioRepository asignacionRepository;
  private final AsignacionSalarioMapper mapper;

  @Override
  public List<AsignacionSalario> findByAnioAndMes(Integer anio, Integer mes) {
    if (anio != null && mes != null) {
      return asignacionRepository.findByAnioAndMes(anio, mes).stream()
        .map(mapper::toDomain)
        .toList();
    }
    return asignacionRepository.findAll().stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public Optional<AsignacionSalario> findById(String id) {
    return asignacionRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public AsignacionSalario save(AsignacionSalario asignacion) {
    AsignacionSalarioMO mo = mapper.toMO(asignacion);
    PlataformaMO plataformaMO = new PlataformaMO();
    plataformaMO.setId(asignacion.getPlataformaId());
    mo.setPlataforma(plataformaMO);
    AsignacionSalarioMO saved = asignacionRepository.save(mo);
    return mapper.toDomain(saved);
  }

  @Override
  public void deleteById(String id) {
    asignacionRepository.deleteById(id);
  }
}
