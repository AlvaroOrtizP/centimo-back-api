package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.BalanceFondoDatasourceMapper;
import com.centimo.api.database.models.BalanceFondoMO;
import com.centimo.api.database.models.InstantaneaMensualMO;
import com.centimo.api.database.repositories.BalanceFondoRepository;
import com.centimo.api.database.repositories.CuentaRepository;
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
  private final CuentaRepository cuentaRepository;
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

    sincronizarInstantaneaFondo(balance.getAnio(), balance.getMes());

    return mapper.toDomain(saved);
  }

  private void sincronizarInstantaneaFondo(Integer anio, Integer mes) {
    List<BalanceFondoMO> balances = balanceFondoRepository.findByAnioAndMes(anio, mes);

    BigDecimal saldoTotal = BigDecimal.ZERO;
    BigDecimal ingresosTotal = BigDecimal.ZERO;
    BigDecimal aportacionTotal = BigDecimal.ZERO;
    BigDecimal gastosTotal = BigDecimal.ZERO;
    for (BalanceFondoMO b : balances) {
      saldoTotal = saldoTotal.add(b.getSaldo() != null ? b.getSaldo() : BigDecimal.ZERO);
      ingresosTotal = ingresosTotal.add(b.getIntereses() != null ? b.getIntereses() : BigDecimal.ZERO);
      aportacionTotal = aportacionTotal.add(b.getAportacion() != null ? b.getAportacion() : BigDecimal.ZERO);
      gastosTotal = gastosTotal.add(b.getRetirada() != null ? b.getRetirada() : BigDecimal.ZERO);
    }

    Optional<InstantaneaMensualMO> optional = instantaneaMensualRepository
        .findByCuentaIdAndAnioAndMes("myinvestor-fondo", anio, mes);

    InstantaneaMensualMO instantanea;
    if (optional.isPresent()) {
      instantanea = optional.get();
    } else {
      instantanea = new InstantaneaMensualMO();
      instantanea.setId(UUID.randomUUID().toString());
      cuentaRepository.findById("myinvestor-fondo").ifPresent(instantanea::setCuenta);
      instantanea.setAnio(anio);
      instantanea.setMes(mes);
    }
    instantanea.setSaldo(saldoTotal);
    instantanea.setIngresos(ingresosTotal);
    instantanea.setAportacion(aportacionTotal);
    instantanea.setGastos(gastosTotal);
    instantaneaMensualRepository.save(instantanea);
  }

  @Override
  public void delete(String id) {
    balanceFondoRepository.findById(id).ifPresent(b -> {
      Integer anio = b.getAnio();
      Integer mes = b.getMes();
      balanceFondoRepository.deleteById(id);
      sincronizarInstantaneaFondo(anio, mes);
    });
  }
}
