package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.RevolutBalanceDatasourceMapper;
import com.centimo.api.database.models.RevolutBalanceMO;
import com.centimo.api.database.repositories.RevolutBalanceRepository;
import com.centimo.api.domain.models.RevolutBalance;
import com.centimo.api.ports.driven.RevolutBalanceDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RevolutBalanceDatasourceAdapter implements RevolutBalanceDrivenPort {

  static final int LIMITE_POR_DEFECTO = 24;

  private final RevolutBalanceRepository revolutBalanceRepository;
  private final RevolutBalanceDatasourceMapper mapper;

  @Override
  public Optional<RevolutBalance> findById(String id) {
    return revolutBalanceRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<RevolutBalance> findByMes(String mes) {
    return revolutBalanceRepository.findByMes(mes).map(mapper::toDomain);
  }

  @Override
  public List<RevolutBalance> findAll(Integer limit, String order) {
    int size = limit != null && limit > 0 ? limit : LIMITE_POR_DEFECTO;
    Sort.Direction direction = "asc".equalsIgnoreCase(order)
        ? Sort.Direction.ASC
        : Sort.Direction.DESC;
    Pageable pageable = PageRequest.of(0, size, Sort.by(direction, "mes"));
    return revolutBalanceRepository.findAll(pageable).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public RevolutBalance guardar(RevolutBalance balance) {
    RevolutBalanceMO entity = balance.getId() != null
        ? revolutBalanceRepository.findById(balance.getId()).orElse(mapper.toEntity(balance))
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

    return mapper.toDomain(revolutBalanceRepository.save(entity));
  }

  @Override
  public void eliminar(String id) {
    revolutBalanceRepository.deleteById(id);
  }
}
