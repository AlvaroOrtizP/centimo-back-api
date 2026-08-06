package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.CrowdlendingInversion;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CrowdlendingDrivingPort {

  List<CrowdlendingInversion> listarPorPlataforma(String platformId);

  List<CrowdlendingInversion> listarTodas();

  @Transactional
  CrowdlendingInversion crear(CrowdlendingInversion inversion);

  @Transactional
  void eliminar(String id);
}
