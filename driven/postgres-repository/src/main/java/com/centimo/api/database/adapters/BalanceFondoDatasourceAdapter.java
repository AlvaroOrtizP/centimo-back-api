package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.BalanceFondoMapper;
import com.centimo.api.database.models.BalanceFondoMO;
import com.centimo.api.database.models.FondoMyInvestorMO;
import com.centimo.api.database.repositories.BalanceFondoRepository;
import com.centimo.api.domain.models.BalanceFondo;
import com.centimo.api.ports.driven.BalanceFondoDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BalanceFondoDatasourceAdapter implements BalanceFondoDrivenPort {

  private final BalanceFondoRepository balanceRepository;
  private final BalanceFondoMapper mapper;

  @Override
  public List<BalanceFondo> findByAnioAndMes(Integer anio, Integer mes) {
    return balanceRepository.findByAnioAndMes(anio, mes).stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public Optional<BalanceFondo> findById(String id) {
    return balanceRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public BalanceFondo save(BalanceFondo balance) {
    BalanceFondoMO mo = mapper.toMO(balance);
    FondoMyInvestorMO fondoMO = new FondoMyInvestorMO();
    fondoMO.setId(balance.getFondoId());
    mo.setFondo(fondoMO);
    BalanceFondoMO saved = balanceRepository.save(mo);
    return mapper.toDomain(saved);
  }

  @Override
  public void deleteById(String id) {
    balanceRepository.deleteById(id);
  }
}
