package com.centimo.api.usecases;

import com.centimo.api.domain.models.FondoMyInvestor;
import com.centimo.api.ports.driven.MyInvestorFundDrivenPort;
import com.centimo.api.ports.driving.MyInvestorFundDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyInvestorFundUseCase implements MyInvestorFundDrivingPort {

  private final MyInvestorFundDrivenPort myInvestorFundDrivenPort;

  @Override
  public List<FondoMyInvestor> listAll() {
    return myInvestorFundDrivenPort.findAll();
  }

  @Override
  public FondoMyInvestor getById(String id) {
    return myInvestorFundDrivenPort.findById(id).orElseThrow();
  }

  @Transactional
  @Override
  public FondoMyInvestor create(FondoMyInvestor fondo) {
    return myInvestorFundDrivenPort.save(fondo);
  }

  @Transactional
  @Override
  public FondoMyInvestor update(String id, FondoMyInvestor fondo) {
    FondoMyInvestor existente = myInvestorFundDrivenPort.findById(id).orElseThrow();

    existente.setCodigoIsin(fondo.getCodigoIsin());
    existente.setNombre(fondo.getNombre());

    return myInvestorFundDrivenPort.save(existente);
  }

  @Transactional
  @Override
  public void delete(String id) {
    myInvestorFundDrivenPort.delete(id);
  }
}
