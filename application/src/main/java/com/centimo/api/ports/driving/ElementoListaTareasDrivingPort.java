package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.ElementoListaTareas;

import java.util.List;

public interface ElementoListaTareasDrivingPort {

  List<ElementoListaTareas> buscarTodos(String instantaneaId);

  ElementoListaTareas crear(String instantaneaId, String texto);

  ElementoListaTareas alternar(String instantaneaId, String elementoId);
}
