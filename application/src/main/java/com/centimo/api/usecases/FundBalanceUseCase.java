package com.centimo.api.usecases;

import com.centimo.api.domain.models.BalanceFondo;
import com.centimo.api.ports.driven.FundBalanceDrivenPort;
import com.centimo.api.ports.driving.FundBalanceDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FundBalanceUseCase implements FundBalanceDrivingPort {

  private final FundBalanceDrivenPort fundBalanceDrivenPort;

  @Override
  public List<BalanceFondo> listByYearAndMonth(Integer anio, Integer mes) {
    return fundBalanceDrivenPort.findByAnioAndMes(anio, mes);
  }

  @Transactional
  @Override
  public BalanceFondo create(BalanceFondo balance) {
    return fundBalanceDrivenPort.save(balance);
  }

  @Transactional
  @Override
  public BalanceFondo update(String id, BalanceFondo balance) {
    BalanceFondo existente = fundBalanceDrivenPort.findById(id).orElseThrow();

    existente.setSaldo(balance.getSaldo());

    return fundBalanceDrivenPort.save(existente);
  }

  @Transactional
  @Override
  public void delete(String id) {
    fundBalanceDrivenPort.delete(id);
  }
}
