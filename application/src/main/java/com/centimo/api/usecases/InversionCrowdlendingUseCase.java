package com.centimo.api.usecases;

import com.centimo.api.domain.exceptions.NotFoundException;
import com.centimo.api.domain.models.InversionCrowdlending;
import com.centimo.api.ports.driven.InversionCrowdlendingDrivenPort;
import com.centimo.api.ports.driving.InversionCrowdlendingDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InversionCrowdlendingUseCase implements InversionCrowdlendingDrivingPort {

  private final InversionCrowdlendingDrivenPort inversionDrivenPort;

  @Override
  @Transactional(readOnly = true)
  public List<InversionCrowdlending> buscarTodas(String plataformaId) {
    return inversionDrivenPort.findByPlataformaId(plataformaId);
  }

  @Override
  @Transactional(readOnly = true)
  public InversionCrowdlending buscarPorId(String id) {
    return inversionDrivenPort.findById(id)
      .orElseThrow(() -> new NotFoundException("InversionCrowdlending", id));
  }

  @Override
  @Transactional
  public InversionCrowdlending crear(InversionCrowdlending inversion) {
    return inversionDrivenPort.save(inversion);
  }

  @Override
  @Transactional
  public void eliminar(String id) {
    buscarPorId(id);
    inversionDrivenPort.deleteById(id);
  }
}
