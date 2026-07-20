package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.ElementoListaTareasMapper;
import com.centimo.api.database.models.ElementoListaTareasMO;
import com.centimo.api.database.models.InstantaneaMensualMO;
import com.centimo.api.database.repositories.ElementoListaTareasRepository;
import com.centimo.api.domain.models.ElementoListaTareas;
import com.centimo.api.ports.driven.ElementoListaTareasDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ElementoListaTareasDatasourceAdapter implements ElementoListaTareasDrivenPort {

  private final ElementoListaTareasRepository elementoRepository;
  private final ElementoListaTareasMapper mapper;

  @Override
  public List<ElementoListaTareas> findByInstantaneaId(String instantaneaId) {
    return elementoRepository.findByInstantaneaIdOrderByOrden(instantaneaId).stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public Optional<ElementoListaTareas> findById(String id) {
    return elementoRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public ElementoListaTareas save(ElementoListaTareas elemento) {
    ElementoListaTareasMO mo = mapper.toMO(elemento);
    InstantaneaMensualMO instantaneaMO = new InstantaneaMensualMO();
    instantaneaMO.setId(elemento.getInstantaneaId());
    mo.setInstantanea(instantaneaMO);
    ElementoListaTareasMO saved = elementoRepository.save(mo);
    return mapper.toDomain(saved);
  }

  @Override
  public void deleteById(String id) {
    elementoRepository.deleteById(id);
  }
}
