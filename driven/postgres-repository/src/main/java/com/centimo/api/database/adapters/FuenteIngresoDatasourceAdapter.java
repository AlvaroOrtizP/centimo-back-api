package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.FuenteIngresoMapper;
import com.centimo.api.database.models.FuenteIngresoMO;
import com.centimo.api.database.models.InstantaneaMensualMO;
import com.centimo.api.database.repositories.FuenteIngresoRepository;
import com.centimo.api.domain.models.FuenteIngreso;
import com.centimo.api.ports.driven.FuenteIngresoDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FuenteIngresoDatasourceAdapter implements FuenteIngresoDrivenPort {

  private final FuenteIngresoRepository fuenteIngresoRepository;
  private final FuenteIngresoMapper mapper;

  @Override
  public List<FuenteIngreso> findByInstantaneaId(String instantaneaId) {
    return fuenteIngresoRepository.findByInstantaneaId(instantaneaId).stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public Optional<FuenteIngreso> findById(String id) {
    return fuenteIngresoRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public FuenteIngreso save(FuenteIngreso ingreso) {
    FuenteIngresoMO mo = mapper.toMO(ingreso);
    InstantaneaMensualMO instantaneaMO = new InstantaneaMensualMO();
    instantaneaMO.setId(ingreso.getInstantaneaId());
    mo.setInstantanea(instantaneaMO);
    FuenteIngresoMO saved = fuenteIngresoRepository.save(mo);
    return mapper.toDomain(saved);
  }

  @Override
  public void deleteById(String id) {
    fuenteIngresoRepository.deleteById(id);
  }
}
