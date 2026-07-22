package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.InstantaneaMensual;

import java.util.Optional;

public interface InstantaneaDrivenPort {
  Optional<InstantaneaMensual> findById(String id);

  Optional<InstantaneaMensual> findByAnioAndMes(String accountId, Integer anio, Integer mes);

  Optional<InstantaneaMensual> findByCompositeKey(String compositeKey);

  InstantaneaMensual guardar(InstantaneaMensual instantanea);
}
