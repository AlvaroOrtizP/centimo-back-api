package com.centimo.api.usecases;

import com.centimo.api.domain.models.CrowdlendingInversion;
import com.centimo.api.ports.driven.CrowdlendingDrivenPort;
import com.centimo.api.ports.driving.CrowdlendingDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CrowdlendingUseCase implements CrowdlendingDrivingPort {

  private final CrowdlendingDrivenPort crowdlendingDrivenPort;

  @Override
  public List<CrowdlendingInversion> listarPorPlataforma(String platformId) {
    if (platformId == null || platformId.isBlank()) {
      return listarTodas();
    }
    return crowdlendingDrivenPort.findByPlataformaId(platformId);
  }

  @Override
  public List<CrowdlendingInversion> listarTodas() {
    return crowdlendingDrivenPort.findAll();
  }

  @Transactional
  @Override
  public CrowdlendingInversion crear(CrowdlendingInversion inversion) {
    return crowdlendingDrivenPort.guardar(inversion);
  }

  @Transactional
  @Override
  public void eliminar(String id) {
    crowdlendingDrivenPort.eliminar(id);
  }
}
