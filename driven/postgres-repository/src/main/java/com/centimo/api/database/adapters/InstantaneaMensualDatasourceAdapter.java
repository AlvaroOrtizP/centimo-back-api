package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.InstantaneaMensualMapper;
import com.centimo.api.database.models.InstantaneaMensualMO;
import com.centimo.api.database.repositories.CuentaRepository;
import com.centimo.api.database.repositories.InstantaneaMensualRepository;
import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.ports.driven.InstantaneaDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstantaneaMensualDatasourceAdapter implements InstantaneaDrivenPort {

  private final InstantaneaMensualRepository instantaneaRepository;
  private final CuentaRepository cuentaRepository;
  private final InstantaneaMensualMapper mapper;

  @Override
  public InstantaneaMensual findByAnioAndMes(Integer anio, Integer mes) {
    var instantaneaMensual = instantaneaRepository.findByAnioAndMes(anio, mes);
    return mapper.toDomain(instantaneaMensual);
  }
}
