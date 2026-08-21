package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.InteresAnualMintos;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface InteresAnualMintosDrivingPort {

  Optional<InteresAnualMintos> obtenerPorAnio(Integer anio);

  List<InteresAnualMintos> listarTodos();

  @Transactional
  InteresAnualMintos crear(InteresAnualMintos interes);

  @Transactional
  InteresAnualMintos actualizar(String id, InteresAnualMintos interes);

  @Transactional
  void eliminar(String id);
}
