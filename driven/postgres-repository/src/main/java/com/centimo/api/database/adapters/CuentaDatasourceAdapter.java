package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.CuentaMapper;
import com.centimo.api.database.repositories.CuentaRepository;
import com.centimo.api.domain.models.Cuenta;
import com.centimo.api.ports.driven.CuentaDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuentaDatasourceAdapter implements CuentaDrivenPort {

  private final CuentaRepository cuentaRepository;
  private final CuentaMapper mapper;

  @Override
  public Optional<Cuenta> findById(String id) {
    return cuentaRepository.findById(id).map(mapper::toDomain);
  }
}
