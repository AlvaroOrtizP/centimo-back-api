package com.centimo.api.usecases;

import com.centimo.api.domain.models.InteresAnualMintos;
import com.centimo.api.ports.driven.InteresAnualMintosDrivenPort;
import com.centimo.api.ports.driving.InteresAnualMintosDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InteresAnualMintosUseCase implements InteresAnualMintosDrivingPort {

  private final InteresAnualMintosDrivenPort interesAnualMintosDrivenPort;

  @Override
  public Optional<InteresAnualMintos> obtenerPorAnio(Integer anio) {
    if (anio == null) {
      return Optional.empty();
    }
    return interesAnualMintosDrivenPort.findByAnio(anio);
  }

  @Override
  public List<InteresAnualMintos> listarTodos() {
    return interesAnualMintosDrivenPort.findAll();
  }

  @Transactional
  @Override
  public InteresAnualMintos crear(InteresAnualMintos interes) {
    return interesAnualMintosDrivenPort.guardar(interes);
  }

  @Transactional
  @Override
  public InteresAnualMintos actualizar(String id, InteresAnualMintos interes) {
    InteresAnualMintos existente = interesAnualMintosDrivenPort.findByAnio(interes.getAnio())
        .filter(e -> e.getId().equals(id))
        .orElseThrow();

    existente.setCantidad(interes.getCantidad());
    existente.setRetencionImpuestos(interes.getRetencionImpuestos());
    existente.setTipoImpositivo(interes.getTipoImpositivo());
    existente.setImporteNeto(interes.getImporteNeto());
    existente.setFechaActualizacion(LocalDateTime.now());

    return interesAnualMintosDrivenPort.guardar(existente);
  }

  @Transactional
  @Override
  public void eliminar(String id) {
    interesAnualMintosDrivenPort.eliminar(id);
  }
}
