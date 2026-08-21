package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.InteresAnualMintos;

import java.util.List;
import java.util.Optional;

public interface InteresAnualMintosDrivenPort {

  Optional<InteresAnualMintos> findByAnio(Integer anio);

  List<InteresAnualMintos> findAll();

  InteresAnualMintos guardar(InteresAnualMintos interes);

  void eliminar(String id);
}
