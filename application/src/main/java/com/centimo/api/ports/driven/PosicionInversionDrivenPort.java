package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.PosicionInversion;

import java.util.List;
import java.util.Optional;

public interface PosicionInversionDrivenPort {

  List<PosicionInversion> findByInstantaneaId(String instantaneaId);

  Optional<PosicionInversion> findById(String id);

  PosicionInversion save(PosicionInversion posicion);

  void deleteById(String id);
}
