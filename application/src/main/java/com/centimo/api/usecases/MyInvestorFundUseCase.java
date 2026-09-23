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
  public List<FondoMyInvestor> listar() {
    return myInvestorFundDrivenPort.findAll();
  }

  @Override
  public FondoMyInvestor obtener(String id) {
    return myInvestorFundDrivenPort.findById(id).orElseThrow();
  }

  @Transactional
  @Override
  public FondoMyInvestor crear(FondoMyInvestor fondo) {
    return myInvestorFundDrivenPort.guardar(fondo);
  }

  @Transactional
  @Override
  public FondoMyInvestor actualizar(String id, FondoMyInvestor fondo) {
    FondoMyInvestor existente = myInvestorFundDrivenPort.findById(id).orElseThrow();
    existente.setCodigoIsin(fondo.getCodigoIsin() != null ? fondo.getCodigoIsin() : existente.getCodigoIsin());
    existente.setNombre(fondo.getNombre() != null ? fondo.getNombre() : existente.getNombre());
    existente.setTipo(fondo.getTipo() != null ? fondo.getTipo() : existente.getTipo());
    return myInvestorFundDrivenPort.guardar(existente);
  }

  @Transactional
  @Override
  public void eliminar(String id) {
    myInvestorFundDrivenPort.eliminar(id);
  }
}