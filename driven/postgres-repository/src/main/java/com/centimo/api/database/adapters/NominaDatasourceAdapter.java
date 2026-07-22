package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.NominaMapper;
import com.centimo.api.database.models.NominaMO;
import com.centimo.api.database.repositories.NominaRepository;
import com.centimo.api.domain.models.Nomina;
import com.centimo.api.ports.driven.NominaDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NominaDatasourceAdapter implements NominaDrivenPort {

  private final NominaRepository nominaRepository;
  private final NominaMapper mapper;

  @Override
  public Optional<Nomina> findByAnioAndMes(Integer anio, Integer mes) {
    return nominaRepository.findByAnioAndMes(anio, mes).map(mapper::toDomain);
  }

  @Override
  public Nomina guardar(Nomina nomina) {
    NominaMO entity = mapper.toMO(nomina);

    if (entity.getId() == null) {
      entity.setId(UUID.randomUUID().toString());
    }

    NominaMO savedEntity = nominaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }
}
