package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.OperacionInversionMapper;
import com.centimo.api.database.models.CuentaMO;
import com.centimo.api.database.models.OperacionInversionMO;
import com.centimo.api.database.repositories.OperacionInversionRepository;
import com.centimo.api.domain.models.OperacionInversion;
import com.centimo.api.ports.driven.OperacionInversionDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperacionInversionDatasourceAdapter implements OperacionInversionDrivenPort {

  private final OperacionInversionRepository operacionRepository;
  private final OperacionInversionMapper mapper;

  @Override
  public List<OperacionInversion> findByCuentaId(String cuentaId) {
    return operacionRepository.findByCuentaId(cuentaId).stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public Optional<OperacionInversion> findById(String id) {
    return operacionRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public OperacionInversion save(OperacionInversion operacion) {
    OperacionInversionMO mo = mapper.toMO(operacion);
    CuentaMO cuentaMO = new CuentaMO();
    cuentaMO.setId(operacion.getCuentaId());
    mo.setCuenta(cuentaMO);
    OperacionInversionMO saved = operacionRepository.save(mo);
    return mapper.toDomain(saved);
  }

  @Override
  public void deleteById(String id) {
    operacionRepository.deleteById(id);
  }
}
