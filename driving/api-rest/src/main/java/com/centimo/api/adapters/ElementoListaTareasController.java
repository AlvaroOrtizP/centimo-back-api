package com.centimo.api.adapters;

import com.centimo.api.domain.models.ElementoListaTareas;
import com.centimo.api.dto.CreateElementoListaTareasRequest;
import com.centimo.api.dto.ElementoListaTareasDto;
import com.centimo.api.mappers.ElementoListaTareasApiMapper;
import com.centimo.api.ports.driving.ElementoListaTareasDrivingPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instantaneas/{instantaneaId}/tareas")
@RequiredArgsConstructor
public class ElementoListaTareasController {

  private final ElementoListaTareasDrivingPort elementoDrivingPort;
  private final ElementoListaTareasApiMapper mapper;

  /** Usado en: Entrada Datos (lista de tareas del mes). */
  @GetMapping
  public ResponseEntity<List<ElementoListaTareasDto>> buscarTodos(
      @PathVariable String instantaneaId) {
    List<ElementoListaTareasDto> dtos = elementoDrivingPort.buscarTodos(instantaneaId).stream()
      .map(mapper::toDto)
      .toList();
    return ResponseEntity.ok(dtos);
  }

  /** Usado en: Entrada Datos (añadir tarea). */
  @PostMapping
  public ResponseEntity<ElementoListaTareasDto> crear(
      @PathVariable String instantaneaId,
      @Valid @RequestBody CreateElementoListaTareasRequest request) {
    ElementoListaTareas elemento = elementoDrivingPort.crear(instantaneaId, request.getText());
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(elemento));
  }

  /** Usado en: Entrada Datos (alternar tarea completada). */
  @PostMapping("/{elementoId}/alternar")
  public ResponseEntity<ElementoListaTareasDto> alternar(
      @PathVariable String instantaneaId,
      @PathVariable String elementoId) {
    ElementoListaTareas elemento = elementoDrivingPort.alternar(instantaneaId, elementoId);
    return ResponseEntity.ok(mapper.toDto(elemento));
  }
}
