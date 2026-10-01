package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.BancoBalanceDatasourceMapper;
import com.centimo.api.database.models.BancoBalanceMO;
import com.centimo.api.database.repositories.BancoBalanceRepository;
import com.centimo.api.domain.models.BancoBalance;
import com.centimo.api.ports.driven.BancoBalanceDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BancoBalanceDatasourceAdapter implements BancoBalanceDrivenPort {

  static final int LIMITE_POR_DEFECTO = 24;

  private final BancoBalanceRepository bancoBalanceRepository;
  private final BancoBalanceDatasourceMapper mapper;

  @Override
  public Optional<BancoBalance> findById(String id) {
    return bancoBalanceRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<BancoBalance> findByEntidadAndMes(String entidad, String mes) {
    return bancoBalanceRepository.findByEntidadAndMes(entidad, mes).map(mapper::toDomain);
  }

  @Override
  public List<BancoBalance> findByEntidad(String entidad, Integer limit, String order) {
    int size = limit != null && limit > 0 ? limit : LIMITE_POR_DEFECTO;
    Sort.Direction direction = "asc".equalsIgnoreCase(order)
        ? Sort.Direction.ASC
        : Sort.Direction.DESC;
    Pageable pageable = PageRequest.of(0, size, Sort.by(direction, "mes"));
    return bancoBalanceRepository.findByEntidad(entidad, pageable).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<BancoBalance> findByEntidadInAndMesIn(List<String> entidades, List<String> meses) {
    return bancoBalanceRepository.findByEntidadInAndMesIn(entidades, meses).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<BancoBalance> findByMesIn(List<String> meses) {
    return bancoBalanceRepository.findByMesIn(meses).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<String> findEntidades() {
    return bancoBalanceRepository.findEntidades();
  }

  @Override
  public BancoBalance guardar(BancoBalance balance) {
    BancoBalanceMO entity = balance.getId() != null
        ? bancoBalanceRepository.findById(balance.getId()).orElse(mapper.toEntity(balance))
        : mapper.toEntity(balance);

    if (entity.getId() == null) {
      entity.setId(balance.getEntidad() + "-" + balance.getMes());
    }

    entity.setEntidad(balance.getEntidad());
    entity.setMes(balance.getMes());
    entity.setBalanceMensual(balance.getBalanceMensual());
    entity.setAporteMensual(balance.getAporteMensual());

    return mapper.toDomain(bancoBalanceRepository.save(entity));
  }

  @Override
  public void eliminar(String id) {
    bancoBalanceRepository.deleteById(id);
  }
}