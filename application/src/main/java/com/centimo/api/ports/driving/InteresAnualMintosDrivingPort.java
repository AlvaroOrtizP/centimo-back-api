package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.InteresAnualMintos;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InteresAnualMintosDrivingPort {

  List<InteresAnualMintos> listar(String mes);

  @Transactional
  InteresAnualMintos crear(InteresAnualMintos interes);

  @Transactional
  InteresAnualMintos actualizar(String id, InteresAnualMintos interes);

  @Transactional
  void eliminar(String id);
}