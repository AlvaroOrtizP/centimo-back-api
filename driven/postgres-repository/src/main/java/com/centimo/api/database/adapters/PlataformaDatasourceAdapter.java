package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.PlataformaMapper;
import com.centimo.api.database.models.PlataformaMO;
import com.centimo.api.database.repositories.PlataformaRepository;
import com.centimo.api.domain.models.Plataforma;
import com.centimo.api.ports.driven.PlataformaDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlataformaDatasourceAdapter implements PlataformaDrivenPort {

  private final PlataformaRepository plataformaRepository;
  private final PlataformaMapper mapper;

  @Override
  public List<Plataforma> findAll() {
    return plataformaRepository.findAll().stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public Optional<Plataforma> findById(String id) {
    return plataformaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Plataforma save(Plataforma plataforma) {
    PlataformaMO mo = mapper.toMO(plataforma);
    PlataformaMO saved = plataformaRepository.save(mo);
    return mapper.toDomain(saved);
  }

  @Override
  public void deleteById(String id) {
    plataformaRepository.deleteById(id);
  }
}
