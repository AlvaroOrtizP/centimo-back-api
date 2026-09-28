package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.BalanceFondoDatasourceMapper;
import com.centimo.api.database.models.BalanceFondoMO;
import com.centimo.api.database.models.FondoMyInvestorMO;
import com.centimo.api.database.repositories.BalanceFondoRepository;
import com.centimo.api.database.repositories.FondoMyInvestorRepository;
import com.centimo.api.domain.models.BalanceFondo;
import com.centimo.api.ports.driven.FundBalanceDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BalanceFondoDatasourceAdapter implements FundBalanceDrivenPort {

  private final BalanceFondoRepository balanceFondoRepository;
  private final FondoMyInvestorRepository fondoMyInvestorRepository;
  private final BalanceFondoDatasourceMapper mapper;

  @Override
  public List<BalanceFondo> findByAnioAndMes(Integer anio, Integer mes) {
    Sort sort = Sort.by(Sort.Direction.ASC, "fondoId");
    return balanceFondoRepository.findByAnioAndMes(anio, mes, sort).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<BalanceFondo> findByAnioInAndMesIn(List<Integer> anios, List<Integer> meses) {
    Sort sort = Sort.by(Sort.Direction.ASC, "anio", "mes", "fondoId");
    return balanceFondoRepository.findByAnioInAndMesIn(anios, meses, sort).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public Optional<BalanceFondo> findById(String id) {
    return balanceFondoRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<BalanceFondo> findByFondoIdAnioAndMes(String fondoId, Integer anio, Integer mes) {
    return balanceFondoRepository.findByFondoIdAndAnioAndMes(fondoId, anio, mes).map(mapper::toDomain);
  }

  @Override
  public BalanceFondo guardar(BalanceFondo balance) {
    FondoMyInvestorMO fondo = fondoMyInvestorRepository.findById(balance.getFondoId()).orElseThrow();

    BalanceFondoMO entity = balance.getId() != null
        ? balanceFondoRepository.findById(balance.getId()).orElse(mapper.toEntity(balance))
        : mapper.toEntity(balance);

    if (entity.getId() == null) {
      entity.setId(UUID.randomUUID().toString());
    }

    entity.setFondo(fondo);
    entity.setAnio(balance.getAnio());
    entity.setMes(balance.getMes());
    entity.setSaldo(balance.getSaldo());
    entity.setIntereses(balance.getIntereses());
    entity.setAportacion(balance.getAportacion());
    entity.setRetirada(balance.getRetirada());

    return mapper.toDomain(balanceFondoRepository.save(entity));
  }

  @Override
  public void eliminar(String id) {
    balanceFondoRepository.deleteById(id);
  }
}