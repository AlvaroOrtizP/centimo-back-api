package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.CrowdlendingInversion;

import java.util.List;
import java.util.Optional;

public interface CrowdlendingDrivenPort {

  List<CrowdlendingInversion> findByPlataformaId(String platformId);

  List<CrowdlendingInversion> findAll();

  Optional<CrowdlendingInversion> findById(String id);

  CrowdlendingInversion guardar(CrowdlendingInversion inversion);

  void eliminar(String id);
}
