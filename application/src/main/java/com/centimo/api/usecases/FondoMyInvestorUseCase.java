package com.centimo.api.usecases;

import com.centimo.api.domain.exceptions.NotFoundException;
import com.centimo.api.domain.models.FondoMyInvestor;
import com.centimo.api.ports.driven.FondoMyInvestorDrivenPort;
import com.centimo.api.ports.driving.FondoMyInvestorDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FondoMyInvestorUseCase implements FondoMyInvestorDrivingPort {

  private final FondoMyInvestorDrivenPort fondoDrivenPort;

  @Override
  @Transactional(readOnly = true)
  public List<FondoMyInvestor> buscarTodos() {
    return fondoDrivenPort.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public FondoMyInvestor buscarPorId(String id) {
    return fondoDrivenPort.findById(id)
      .orElseThrow(() -> new NotFoundException("FondoMyInvestor", id));
  }

  @Override
  @Transactional
  public FondoMyInvestor crear(FondoMyInvestor fondo) {
    return fondoDrivenPort.save(fondo);
  }

  @Override
  @Transactional
  public FondoMyInvestor actualizar(String id, FondoMyInvestor fondo) {
    buscarPorId(id);
    fondo.setId(id);
    return fondoDrivenPort.save(fondo);
  }

  @Override
  @Transactional
  public void eliminar(String id) {
    buscarPorId(id);
    fondoDrivenPort.deleteById(id);
  }
}
