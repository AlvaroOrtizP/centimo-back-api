package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.BalanceFondoDatasourceMapper;
import com.centimo.api.database.models.BalanceFondoMO;
import com.centimo.api.database.repositories.BalanceFondoRepository;
import com.centimo.api.database.repositories.FondoMyInvestorRepository;
import com.centimo.api.database.repositories.InstantaneaMensualRepository;
import com.centimo.api.domain.models.BalanceFondo;
import com.centimo.api.ports.driven.FundBalanceDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BalanceFondoDatasourceAdapter implements FundBalanceDrivenPort {

  private final BalanceFondoRepository balanceFondoRepository;
  private final InstantaneaMensualRepository instantaneaMensualRepository;
  private final FondoMyInvestorRepository fondoMyInvestorRepository;
  private final BalanceFondoDatasourceMapper mapper;

  @Override
  public List<BalanceFondo> findByAnioAndMes(Integer anio, Integer mes) {
    return balanceFondoRepository.findByAnioAndMes(anio, mes).stream()
            .map(mapper::toDomain)
            .toList();
  }

  @Override
  public Optional<BalanceFondo> findById(String id) {
    return balanceFondoRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public BalanceFondo save(BalanceFondo balance) {
    BalanceFondoMO entity = balance.getId() != null
            ? balanceFondoRepository.findById(balance.getId()).orElse(mapper.toEntity(balance))
            : mapper.toEntity(balance);

    if (entity.getId() == null) {
      entity.setId(UUID.randomUUID().toString());
    }

    entity.setAnio(balance.getAnio());
    entity.setMes(balance.getMes());
    entity.setSaldo(balance.getSaldo());

    fondoMyInvestorRepository.findById(balance.getFondoId())
            .ifPresent(entity::setFondo);

    BalanceFondoMO saved = balanceFondoRepository.save(entity);

    var optional = instantaneaMensualRepository.findByCuentaIdAndAnioAndMes("myinvestor-fondo", balance.getAnio(), balance.getMes());
    if(optional.isPresent()){
      var instantanea =  optional.get();
      instantanea.setSaldo(instantanea.getSaldo().add(balance.getSaldo()));
      instantanea.setAportacion(instantanea.getAportacion().add(balance.getAportacion()));
      instantaneaMensualRepository.save(instantanea);
    }else{

    }

    return mapper.toDomain(saved);
  }

  @Override
  public void delete(String id) {
    balanceFondoRepository.deleteById(id);
  }
}
