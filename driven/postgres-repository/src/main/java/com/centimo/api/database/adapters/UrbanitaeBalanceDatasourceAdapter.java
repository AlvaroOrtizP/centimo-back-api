package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.UrbanitaeBalanceDatasourceMapper;
import com.centimo.api.database.models.UrbanitaeBalanceMO;
import com.centimo.api.database.repositories.UrbanitaeBalanceRepository;
import com.centimo.api.domain.models.UrbanitaeBalance;
import com.centimo.api.ports.driven.UrbanitaeBalanceDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrbanitaeBalanceDatasourceAdapter implements UrbanitaeBalanceDrivenPort {

  static final int LIMITE_POR_DEFECTO = 24;

  private final UrbanitaeBalanceRepository urbanitaeBalanceRepository;
  private final UrbanitaeBalanceDatasourceMapper mapper;

  @Override
  public Optional<UrbanitaeBalance> findById(String id) {
    return urbanitaeBalanceRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<UrbanitaeBalance> findByMes(String mes) {
    return urbanitaeBalanceRepository.findByMes(mes).map(mapper::toDomain);
  }

  @Override
  public List<UrbanitaeBalance> findAll(Integer limit, String order) {
    int size = limit != null && limit > 0 ? limit : LIMITE_POR_DEFECTO;
    Sort.Direction direction = "asc".equalsIgnoreCase(order)
        ? Sort.Direction.ASC
        : Sort.Direction.DESC;
    Pageable pageable = PageRequest.of(0, size, Sort.by(direction, "mes"));
    return urbanitaeBalanceRepository.findAll(pageable).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public UrbanitaeBalance guardar(UrbanitaeBalance balance) {
    UrbanitaeBalanceMO entity = balance.getId() != null
        ? urbanitaeBalanceRepository.findById(balance.getId()).orElse(mapper.toEntity(balance))
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

    return mapper.toDomain(urbanitaeBalanceRepository.save(entity));
  }

  @Override
  public void eliminar(String id) {
    urbanitaeBalanceRepository.deleteById(id);
  }
}