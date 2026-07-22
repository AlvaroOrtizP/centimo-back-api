package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.Cuenta;

import java.util.Optional;

public interface CuentaDrivenPort {
  Optional<Cuenta> findById(String id);
}
