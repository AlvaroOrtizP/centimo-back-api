package com.centimo.api.usecases;

import com.centimo.api.domain.models.InteresAnualMintos;
import com.centimo.api.ports.driven.InteresAnualMintosDrivenPort;
import com.centimo.api.ports.driving.InteresAnualMintosDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InteresAnualMintosUseCase implements InteresAnualMintosDrivingPort {

  private final InteresAnualMintosDrivenPort interesAnualMintosDrivenPort;

  @Override
  public List<InteresAnualMintos> listar(String mes) {
    if (mes == null) {
      return interesAnualMintosDrivenPort.findAll();
    }
    return interesAnualMintosDrivenPort.findByMes(mes).stream().toList();
  }

  @Transactional
  @Override
  public InteresAnualMintos crear(InteresAnualMintos interes) {
    if (interes.getImporteAñadido() == null) {
      interes.setImporteAñadido(BigDecimal.ZERO);
    }
    Optional<InteresAnualMintos> existente = interesAnualMintosDrivenPort.findByMes(interes.getMes());
    if (existente.isPresent()) {
      InteresAnualMintos actualizado = actualizar(existente.get().getId(), interes);
      actualizado.setFechaCreacion(existente.get().getFechaCreacion());
      return actualizado;
    }
    interes.setId(UUID.randomUUID().toString());
    return interesAnualMintosDrivenPort.guardar(interes);
  }

  @Transactional
  @Override
  public InteresAnualMintos actualizar(String id, InteresAnualMintos interes) {
    InteresAnualMintos existente = interesAnualMintosDrivenPort.findById(id).orElseThrow();
    if (interes.getImporteAñadido() != null) {
      existente.setImporteAñadido(interes.getImporteAñadido());
    }
    if (interes.getValorFinal() != null) {
      existente.setValorFinal(interes.getValorFinal());
    }
    existente.setFechaActualizacion(LocalDateTime.now());
    return interesAnualMintosDrivenPort.guardar(existente);
  }

  @Transactional
  @Override
  public void eliminar(String id) {
    interesAnualMintosDrivenPort.eliminar(id);
  }
}