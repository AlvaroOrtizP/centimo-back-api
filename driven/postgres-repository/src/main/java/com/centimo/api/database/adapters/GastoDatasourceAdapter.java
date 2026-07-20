package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.GastoMapper;
import com.centimo.api.database.models.GastoMO;
import com.centimo.api.database.models.InstantaneaMensualMO;
import com.centimo.api.database.repositories.GastoRepository;
import com.centimo.api.database.repositories.InstantaneaMensualRepository;
import com.centimo.api.domain.models.Gasto;
import com.centimo.api.ports.driven.GastoDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GastoDatasourceAdapter implements GastoDrivenPort {

  private final GastoRepository gastoRepository;
  private final InstantaneaMensualRepository instantaneaRepository;
  private final GastoMapper mapper;

  @Override
  public List<Gasto> findByInstantaneaId(String instantaneaId) {
    return gastoRepository.findByInstantaneaId(instantaneaId).stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public Optional<Gasto> findById(String id) {
    return gastoRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Gasto save(Gasto gasto) {
    GastoMO mo = mapper.toMO(gasto);
    InstantaneaMensualMO instantaneaMO = new InstantaneaMensualMO();
    instantaneaMO.setId(gasto.getInstantaneaId());
    mo.setInstantanea(instantaneaMO);
    GastoMO saved = gastoRepository.save(mo);
    return mapper.toDomain(saved);
  }

  @Override
  public void deleteById(String id) {
    gastoRepository.deleteById(id);
  }
}
