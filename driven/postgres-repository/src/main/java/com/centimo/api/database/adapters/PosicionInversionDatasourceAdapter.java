package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.PosicionInversionMapper;
import com.centimo.api.database.models.InstantaneaMensualMO;
import com.centimo.api.database.models.PosicionInversionMO;
import com.centimo.api.database.repositories.PosicionInversionRepository;
import com.centimo.api.domain.models.PosicionInversion;
import com.centimo.api.ports.driven.PosicionInversionDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PosicionInversionDatasourceAdapter implements PosicionInversionDrivenPort {

  private final PosicionInversionRepository posicionRepository;
  private final PosicionInversionMapper mapper;

  @Override
  public List<PosicionInversion> findByInstantaneaId(String instantaneaId) {
    return posicionRepository.findByInstantaneaId(instantaneaId).stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public Optional<PosicionInversion> findById(String id) {
    return posicionRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public PosicionInversion save(PosicionInversion posicion) {
    PosicionInversionMO mo = mapper.toMO(posicion);
    InstantaneaMensualMO instantaneaMO = new InstantaneaMensualMO();
    instantaneaMO.setId(posicion.getInstantaneaId());
    mo.setInstantanea(instantaneaMO);
    PosicionInversionMO saved = posicionRepository.save(mo);
    return mapper.toDomain(saved);
  }

  @Override
  public void deleteById(String id) {
    posicionRepository.deleteById(id);
  }
}
