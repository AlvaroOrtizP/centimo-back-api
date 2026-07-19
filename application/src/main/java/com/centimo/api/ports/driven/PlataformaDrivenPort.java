package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.Plataforma;

import java.util.List;
import java.util.Optional;

public interface PlataformaDrivenPort {

  List<Plataforma> findAll();

  Optional<Plataforma> findById(String id);

  Plataforma save(Plataforma plataforma);

  void deleteById(String id);
}
