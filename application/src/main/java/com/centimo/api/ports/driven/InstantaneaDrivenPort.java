package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.InstantaneaMensual;

import java.util.Optional;

public interface InstantaneaDrivenPort {
  Optional<InstantaneaMensual> findByAnioAndMes(String accountId, Integer anio, Integer mes);

  InstantaneaMensual guardar(InstantaneaMensual instantanea);
}
