package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.ElementoListaTareas;

import java.util.List;
import java.util.Optional;

public interface ElementoListaTareasDrivenPort {

  List<ElementoListaTareas> findByInstantaneaId(String instantaneaId);

  Optional<ElementoListaTareas> findById(String id);

  ElementoListaTareas save(ElementoListaTareas elemento);

  void deleteById(String id);
}
