package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.NominaDatasourceMapper;
import com.centimo.api.database.models.NominaMO;
import com.centimo.api.database.repositories.NominaRepository;
import com.centimo.api.domain.models.Nomina;
import com.centimo.api.ports.driven.NominaDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NominaDatasourceAdapter implements NominaDrivenPort {

  private final NominaRepository nominaRepository;
  private final NominaDatasourceMapper mapper;

  @Override
  public Optional<Nomina> findByMes(String mes) {
    return nominaRepository.findByMes(mes).map(mapper::toDomain);
  }

  @Override
  public List<Nomina> findAll() {
    return nominaRepository.findAll(Sort.by(Sort.Direction.DESC, "mes")).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public Nomina guardar(Nomina nomina) {
    NominaMO entity = nominaRepository.findByMes(nomina.getMes())
        .orElseGet(() -> mapper.toEntity(nomina));

    entity.setCantidad(nomina.getCantidad());
    entity.setNota(nomina.getNota());

    return mapper.toDomain(nominaRepository.save(entity));
  }

  @Override
  public void eliminar(String mes) {
    nominaRepository.deleteById(mes);
  }
}