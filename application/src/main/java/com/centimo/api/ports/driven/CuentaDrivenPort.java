package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.Cuenta;

import java.util.List;
import java.util.Optional;

public interface CuentaDrivenPort {

  List<Cuenta> findAll();

  List<Cuenta> findByPlataformaId(String plataformaId);

  Optional<Cuenta> findById(String id);
}
