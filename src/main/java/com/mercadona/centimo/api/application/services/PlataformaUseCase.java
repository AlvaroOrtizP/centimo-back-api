package com.mercadona.centimo.api.application.services;

import com.mercadona.centimo.api.driven.jpa.Plataforma;
import com.mercadona.centimo.api.driven.jpa.PlataformaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlataformaUseCase {

  private final PlataformaRepository plataformaRepository;

  @Transactional(readOnly = true)
  public List<Plataforma> buscarTodas() {
    return plataformaRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Plataforma buscarPorId(String id) {
    return plataformaRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Plataforma no encontrada: " + id));
  }

  @Transactional
  public Plataforma crear(Plataforma plataforma) {
    return plataformaRepository.save(plataforma);
  }

  @Transactional
  public Plataforma actualizar(String id, Plataforma actualizada) {
    Plataforma existente = buscarPorId(id);
    existente.setNombre(actualizada.getNombre());
    existente.setTipo(actualizada.getTipo());
    existente.setColor(actualizada.getColor());
    existente.setIcono(actualizada.getIcono());
    existente.setOrden(actualizada.getOrden());
    existente.setNotasFijas(actualizada.getNotasFijas());
    return plataformaRepository.save(existente);
  }

  @Transactional
  public void eliminar(String id) {
    plataformaRepository.deleteById(id);
  }
}