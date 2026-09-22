package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.EquitoBalanceDatasourceMapper;
import com.centimo.api.database.models.EquitoBalanceMO;
import com.centimo.api.database.repositories.EquitoBalanceRepository;
import com.centimo.api.domain.models.EquitoBalance;
import com.centimo.api.ports.driven.EquitoBalanceDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquitoBalanceDatasourceAdapter implements EquitoBalanceDrivenPort {

  static final int LIMITE_POR_DEFECTO = 24;

  private final EquitoBalanceRepository equitoBalanceRepository;
  private final EquitoBalanceDatasourceMapper mapper;

  @Override
  public Optional<EquitoBalance> findById(String id) {
    return equitoBalanceRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<EquitoBalance> findByMes(String mes) {
    return equitoBalanceRepository.findByMes(mes).map(mapper::toDomain);
  }

  @Override
  public List<EquitoBalance> findAll(Integer limit, String order) {
    int size = limit != null && limit > 0 ? limit : LIMITE_POR_DEFECTO;
    Sort.Direction direction = "asc".equalsIgnoreCase(order)
        ? Sort.Direction.ASC
        : Sort.Direction.DESC;
    Pageable pageable = PageRequest.of(0, size, Sort.by(direction, "mes"));
    return equitoBalanceRepository.findAll(pageable).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public EquitoBalance guardar(EquitoBalance balance) {
    EquitoBalanceMO entity = balance.getId() != null
        ? equitoBalanceRepository.findById(balance.getId()).orElse(mapper.toEntity(balance))
        : mapper.toEntity(balance);

    if (entity.getId() == null) {
      entity.setId(balance.getMes());
    }

    entity.setMes(balance.getMes());
    entity.setBalanceMensual(balance.getBalanceMensual());
    entity.setAporteMensual(balance.getAporteMensual());
    entity.setDineroTotal(balance.getDineroTotal());
    entity.setDineroHacienda(balance.getDineroHacienda());
    entity.setDineroFinal(balance.getDineroFinal());

    return mapper.toDomain(equitoBalanceRepository.save(entity));
  }

  @Override
  public void eliminar(String id) {
    equitoBalanceRepository.deleteById(id);
  }
}