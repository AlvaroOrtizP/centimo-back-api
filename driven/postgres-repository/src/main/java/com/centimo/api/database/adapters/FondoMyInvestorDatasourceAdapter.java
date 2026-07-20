package com.centimo.api.database.adapters;

import com.centimo.api.database.mappers.FondoMyInvestorMapper;
import com.centimo.api.database.models.FondoMyInvestorMO;
import com.centimo.api.database.repositories.FondoMyInvestorRepository;
import com.centimo.api.domain.models.FondoMyInvestor;
import com.centimo.api.ports.driven.FondoMyInvestorDrivenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FondoMyInvestorDatasourceAdapter implements FondoMyInvestorDrivenPort {

  private final FondoMyInvestorRepository fondoRepository;
  private final FondoMyInvestorMapper mapper;

  @Override
  public List<FondoMyInvestor> findAll() {
    return fondoRepository.findAll().stream()
      .map(mapper::toDomain)
      .toList();
  }

  @Override
  public Optional<FondoMyInvestor> findById(String id) {
    return fondoRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public FondoMyInvestor save(FondoMyInvestor fondo) {
    FondoMyInvestorMO mo = mapper.toMO(fondo);
    FondoMyInvestorMO saved = fondoRepository.save(mo);
    return mapper.toDomain(saved);
  }

  @Override
  public void deleteById(String id) {
    fondoRepository.deleteById(id);
  }
}
