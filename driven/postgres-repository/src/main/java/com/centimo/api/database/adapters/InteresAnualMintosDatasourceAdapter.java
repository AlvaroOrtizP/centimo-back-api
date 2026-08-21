package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.InteresAnualMintosDatasourceMapper;
import com.centimo.api.database.models.InteresAnualMintosMO;
import com.centimo.api.database.repositories.InteresAnualMintosRepository;
import com.centimo.api.domain.models.InteresAnualMintos;
import com.centimo.api.ports.driven.InteresAnualMintosDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InteresAnualMintosDatasourceAdapter implements InteresAnualMintosDrivenPort {

  private final InteresAnualMintosRepository interesAnualMintosRepository;
  private final InteresAnualMintosDatasourceMapper mapper;

  @Override
  public Optional<InteresAnualMintos> findByAnio(Integer anio) {
    return interesAnualMintosRepository.findByAnio(anio).map(mapper::toDomain);
  }

  @Override
  public List<InteresAnualMintos> findAll() {
    return interesAnualMintosRepository.findAll().stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public InteresAnualMintos guardar(InteresAnualMintos interes) {
    InteresAnualMintosMO entity = interes.getId() != null
        ? interesAnualMintosRepository.findById(interes.getId()).orElse(mapper.toEntity(interes))
        : mapper.toEntity(interes);

    if (entity.getId() == null) {
      entity.setId(UUID.randomUUID().toString());
    }

    entity.setAnio(interes.getAnio());
    entity.setCantidad(interes.getCantidad());
    entity.setRetencionImpuestos(interes.getRetencionImpuestos());
    entity.setTipoImpositivo(interes.getTipoImpositivo());
    entity.setImporteNeto(interes.getImporteNeto());

    InteresAnualMintosMO saved = interesAnualMintosRepository.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public void eliminar(String id) {
    interesAnualMintosRepository.deleteById(id);
  }
}
