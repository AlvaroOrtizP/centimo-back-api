package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.CompromisoMapper;
import com.centimo.api.database.models.CompromisoMO;
import com.centimo.api.database.repositories.CompromisoRepository;
import com.centimo.api.domain.models.Compromiso;
import com.centimo.api.ports.driven.CompromisoDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompromisoDatasourceAdapter implements CompromisoDrivenPort {

  private final CompromisoRepository compromisoRepository;
  private final CompromisoMapper mapper;

  @Override
  public List<Compromiso> findAll() {
    return compromisoRepository.findAll().stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public List<Compromiso> findByMes(Integer mes) {
    return compromisoRepository.findByMes(mes).stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public Optional<Compromiso> findById(String id) {
    return compromisoRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Compromiso save(Compromiso compromiso) {
    CompromisoMO mo = mapper.toMO(compromiso);
    CompromisoMO saved = compromisoRepository.save(mo);
    return mapper.toDomain(saved);
  }

  @Override
  public void deleteById(String id) {
    compromisoRepository.deleteById(id);
  }
}
