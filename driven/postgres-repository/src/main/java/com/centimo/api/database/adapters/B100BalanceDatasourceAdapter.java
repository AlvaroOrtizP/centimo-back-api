package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.B100BalanceDatasourceMapper;
import com.centimo.api.database.models.B100BalanceMO;
import com.centimo.api.database.repositories.B100BalanceRepository;
import com.centimo.api.domain.enums.TipoSubcuentaB100;
import com.centimo.api.domain.models.B100Balance;
import com.centimo.api.ports.driven.B100BalanceDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class B100BalanceDatasourceAdapter implements B100BalanceDrivenPort {

  static final int LIMITE_POR_DEFECTO = 24;

  private final B100BalanceRepository b100BalanceRepository;
  private final B100BalanceDatasourceMapper mapper;

  @Override
  public Optional<B100Balance> findById(String id) {
    return b100BalanceRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<B100Balance> findByTipoSubcuentaAndMes(TipoSubcuentaB100 tipoSubcuenta, String mes) {
    return b100BalanceRepository.findByTipoSubcuentaAndMes(tipoSubcuenta, mes).map(mapper::toDomain);
  }

  @Override
  public List<B100Balance> findByTipoSubcuenta(TipoSubcuentaB100 tipoSubcuenta, String mes, Integer limit, String order) {
    int size = limit != null && limit > 0 ? limit : LIMITE_POR_DEFECTO;
    boolean ascendente = "asc".equalsIgnoreCase(order);
    Sort.Direction direction = ascendente
        ? Sort.Direction.ASC
        : Sort.Direction.DESC;
    Pageable pageable = PageRequest.of(0, size, Sort.by(direction, "mes"));
    List<B100BalanceMO> rows = ascendente
        ? b100BalanceRepository.findByTipoSubcuentaAndMesGreaterThanEqual(tipoSubcuenta, mes, pageable)
        : b100BalanceRepository.findByTipoSubcuentaAndMesLessThanEqual(tipoSubcuenta, mes, pageable);
    return rows.stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public B100Balance guardar(B100Balance balance) {
    B100BalanceMO entity = balance.getId() != null
        ? b100BalanceRepository.findById(balance.getId()).orElse(mapper.toEntity(balance))
        : mapper.toEntity(balance);

    if (entity.getId() == null) {
      entity.setId(balance.getTipoSubcuenta().name() + "-" + balance.getMes());
    }

    entity.setTipoSubcuenta(balance.getTipoSubcuenta());
    entity.setMes(balance.getMes());
    entity.setBalanceMensual(balance.getBalanceMensual());
    entity.setAporteMensual(balance.getAporteMensual());
    entity.setDineroHacienda(balance.getDineroHacienda());
    entity.setDineroTotalRepartir(balance.getDineroTotalRepartir());
    entity.setPorcentajeHacienda(balance.getPorcentajeHacienda());

    return mapper.toDomain(b100BalanceRepository.save(entity));
  }

  @Override
  public void eliminar(String id) {
    b100BalanceRepository.deleteById(id);
  }
}