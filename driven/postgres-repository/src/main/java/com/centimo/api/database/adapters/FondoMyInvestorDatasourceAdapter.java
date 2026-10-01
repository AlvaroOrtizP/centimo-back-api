package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.FondoMyInvestorDatasourceMapper;
import com.centimo.api.database.models.FondoMyInvestorMO;
import com.centimo.api.database.repositories.FondoMyInvestorRepository;
import com.centimo.api.domain.models.FondoMyInvestor;
import com.centimo.api.ports.driven.MyInvestorFundDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FondoMyInvestorDatasourceAdapter implements MyInvestorFundDrivenPort {

  private final FondoMyInvestorRepository fondoMyInvestorRepository;
  private final FondoMyInvestorDatasourceMapper mapper;

  @Override
  public List<FondoMyInvestor> findAll() {
    return fondoMyInvestorRepository.findAll().stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public Optional<FondoMyInvestor> findById(String id) {
    return fondoMyInvestorRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public FondoMyInvestor guardar(FondoMyInvestor fondo) {
    FondoMyInvestorMO entity = fondo.getId() != null
        ? fondoMyInvestorRepository.findById(fondo.getId()).orElse(mapper.toEntity(fondo))
        : mapper.toEntity(fondo);

    entity.setCodigoIsin(fondo.getCodigoIsin());
    entity.setNombre(fondo.getNombre());
    entity.setTipo(fondo.getTipo());

    return mapper.toDomain(fondoMyInvestorRepository.save(entity));
  }

  @Override
  public void eliminar(String id) {
    fondoMyInvestorRepository.deleteById(id);
  }
}