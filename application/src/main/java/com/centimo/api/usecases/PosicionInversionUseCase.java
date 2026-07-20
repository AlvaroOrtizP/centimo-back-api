package com.centimo.api.usecases;

import com.centimo.api.domain.exceptions.NotFoundException;
import com.centimo.api.domain.models.PosicionInversion;
import com.centimo.api.ports.driven.PosicionInversionDrivenPort;
import com.centimo.api.ports.driving.PosicionInversionDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PosicionInversionUseCase implements PosicionInversionDrivingPort {

  private final PosicionInversionDrivenPort posicionDrivenPort;

  @Override
  @Transactional(readOnly = true)
  public List<PosicionInversion> buscarTodas(String instantaneaId) {
    return posicionDrivenPort.findByInstantaneaId(instantaneaId);
  }

  @Override
  @Transactional(readOnly = true)
  public PosicionInversion buscarPorId(String id) {
    return posicionDrivenPort.findById(id)
      .orElseThrow(() -> new NotFoundException("PosicionInversion", id));
  }

  @Override
  @Transactional
  public PosicionInversion crear(PosicionInversion posicion) {
    return posicionDrivenPort.save(posicion);
  }

  @Override
  @Transactional
  public void eliminar(String id) {
    buscarPorId(id);
    posicionDrivenPort.deleteById(id);
  }
}
