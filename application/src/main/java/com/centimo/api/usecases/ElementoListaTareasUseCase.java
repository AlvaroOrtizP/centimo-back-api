package com.centimo.api.usecases;

import com.centimo.api.domain.exceptions.NotFoundException;
import com.centimo.api.domain.models.ElementoListaTareas;
import com.centimo.api.ports.driven.ElementoListaTareasDrivenPort;
import com.centimo.api.ports.driving.ElementoListaTareasDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ElementoListaTareasUseCase implements ElementoListaTareasDrivingPort {

  private final ElementoListaTareasDrivenPort elementoDrivenPort;

  @Override
  @Transactional(readOnly = true)
  public List<ElementoListaTareas> buscarTodos(String instantaneaId) {
    return elementoDrivenPort.findByInstantaneaId(instantaneaId);
  }

  @Override
  @Transactional
  public ElementoListaTareas crear(String instantaneaId, String texto) {
    List<ElementoListaTareas> existentes = elementoDrivenPort.findByInstantaneaId(instantaneaId);
    int siguienteOrden = existentes.stream()
      .mapToInt(ElementoListaTareas::getOrden)
      .max()
      .orElse(0) + 1;

    ElementoListaTareas elemento = ElementoListaTareas.builder()
      .id(UUID.randomUUID().toString())
      .instantaneaId(instantaneaId)
      .texto(texto)
      .marcado(false)
      .orden(siguienteOrden)
      .build();
    return elementoDrivenPort.save(elemento);
  }

  @Override
  @Transactional
  public ElementoListaTareas alternar(String instantaneaId, String elementoId) {
    ElementoListaTareas elemento = elementoDrivenPort.findById(elementoId)
      .orElseThrow(() -> new NotFoundException("ElementoListaTareas", elementoId));
    if (!elemento.getInstantaneaId().equals(instantaneaId)) {
      throw new NotFoundException("ElementoListaTareas", elementoId);
    }
    elemento.setMarcado(!elemento.getMarcado());
    return elementoDrivenPort.save(elemento);
  }
}
