package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.Compromiso;

import java.util.List;
import java.util.Optional;

public interface CompromisoDrivenPort {

  List<Compromiso> findAll();

  List<Compromiso> findByMes(Integer mes);

  Optional<Compromiso> findById(String id);

  Compromiso save(Compromiso compromiso);

  void deleteById(String id);
}
