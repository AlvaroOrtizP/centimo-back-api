package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.InstantaneaMensual;

import java.util.List;
import java.util.Optional;

public interface InstantaneaDrivenPort {

  List<InstantaneaMensual> findAll();

  List<InstantaneaMensual> findByAnioAndMes(Integer anio, Integer mes);

  List<InstantaneaMensual> findByCuentaId(String cuentaId);

  List<InstantaneaMensual> findByAnioMesAndCuentaId(Integer anio, Integer mes, String cuentaId);

  Optional<InstantaneaMensual> findByCuentaIdAndAnioAndMes(String cuentaId, Integer anio, Integer mes);

  Optional<InstantaneaMensual> findById(String id);

  InstantaneaMensual save(InstantaneaMensual instantanea);

  void deleteById(String id);
}
