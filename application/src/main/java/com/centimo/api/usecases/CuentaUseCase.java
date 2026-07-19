package com.centimo.api.usecases;

import com.centimo.api.domain.exceptions.NotFoundException;
import com.centimo.api.domain.models.Cuenta;
import com.centimo.api.ports.driven.CuentaDrivenPort;
import com.centimo.api.ports.driving.CuentaDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaUseCase implements CuentaDrivingPort {

  private final CuentaDrivenPort cuentaDrivenPort;

  @Override
  @Transactional(readOnly = true)
  public List<Cuenta> buscarTodas(String plataformaId) {
    if (plataformaId != null) {
      return cuentaDrivenPort.findByPlataformaId(plataformaId);
    }
    return cuentaDrivenPort.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Cuenta buscarPorId(String id) {
    return cuentaDrivenPort.findById(id)
      .orElseThrow(() -> new NotFoundException("Cuenta", id));
  }

  @Override
  @Transactional
  public Cuenta crear(Cuenta cuenta) {
    return cuentaDrivenPort.save(cuenta);
  }

  @Override
  @Transactional
  public Cuenta actualizar(String id, Cuenta actualizada) {
    Cuenta existente = buscarPorId(id);
    existente.setNombre(actualizada.getNombre());
    existente.setTipo(actualizada.getTipo());
    existente.setMoneda(actualizada.getMoneda());
    existente.setOrden(actualizada.getOrden());
    existente.setPlataformaId(actualizada.getPlataformaId());
    return cuentaDrivenPort.save(existente);
  }

  @Override
  @Transactional
  public void eliminar(String id) {
    cuentaDrivenPort.deleteById(id);
  }
}
