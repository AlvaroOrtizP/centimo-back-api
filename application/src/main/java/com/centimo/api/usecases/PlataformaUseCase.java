package com.centimo.api.usecases;

import com.centimo.api.domain.exceptions.NotFoundException;
import com.centimo.api.domain.models.Plataforma;
import com.centimo.api.ports.driven.PlataformaDrivenPort;
import com.centimo.api.ports.driving.PlataformaDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlataformaUseCase implements PlataformaDrivingPort {

  private final PlataformaDrivenPort plataformaDrivenPort;

  @Override
  @Transactional(readOnly = true)
  public List<Plataforma> buscarTodas() {
    return plataformaDrivenPort.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Plataforma buscarPorId(String id) {
    return plataformaDrivenPort.findById(id)
      .orElseThrow(() -> new NotFoundException("Plataforma", id));
  }

  @Override
  @Transactional
  public Plataforma crear(Plataforma plataforma) {
    return plataformaDrivenPort.save(plataforma);
  }

  @Override
  @Transactional
  public Plataforma actualizar(String id, Plataforma actualizada) {
    Plataforma existente = buscarPorId(id);
    existente.setNombre(actualizada.getNombre());
    existente.setTipo(actualizada.getTipo());
    existente.setColor(actualizada.getColor());
    existente.setIcono(actualizada.getIcono());
    existente.setOrden(actualizada.getOrden());
    existente.setNotasFijas(actualizada.getNotasFijas());
    return plataformaDrivenPort.save(existente);
  }

  @Override
  @Transactional
  public void eliminar(String id) {
    plataformaDrivenPort.deleteById(id);
  }
}
