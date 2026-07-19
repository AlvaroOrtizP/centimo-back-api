package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.CuentaMapper;
import com.centimo.api.database.models.CuentaMO;
import com.centimo.api.database.repositories.CuentaRepository;
import com.centimo.api.database.repositories.PlataformaRepository;
import com.centimo.api.domain.models.Cuenta;
import com.centimo.api.ports.driven.CuentaDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuentaDatasourceAdapter implements CuentaDrivenPort {

  private final CuentaRepository cuentaRepository;
  private final PlataformaRepository plataformaRepository;
  private final CuentaMapper mapper;

  @Override
  public List<Cuenta> findAll() {
    return cuentaRepository.findAll().stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public List<Cuenta> findByPlataformaId(String plataformaId) {
    return cuentaRepository.findByPlataformaId(plataformaId).stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public Optional<Cuenta> findById(String id) {
    return cuentaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Cuenta save(Cuenta cuenta) {
    CuentaMO mo = mapper.toMO(cuenta);
    if (cuenta.getPlataformaId() != null && mo.getPlataforma() == null) {
      mo.setPlataforma(plataformaRepository.getReferenceById(cuenta.getPlataformaId()));
    }
    CuentaMO saved = cuentaRepository.save(mo);
    return mapper.toDomain(saved);
  }

  @Override
  public void deleteById(String id) {
    cuentaRepository.deleteById(id);
  }
}
