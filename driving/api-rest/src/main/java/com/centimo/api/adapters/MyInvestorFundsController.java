package com.centimo.api.adapters;

import com.centimo.api.MyInvestorFundsApi;
import com.centimo.api.domain.models.FondoMyInvestor;
import com.centimo.api.dto.MyInvestorFund;
import com.centimo.api.dto.MyInvestorFundCreate;
import com.centimo.api.dto.MyInvestorFundUpdate;
import com.centimo.api.mappers.MyInvestorFundApiMapper;
import com.centimo.api.ports.driving.MyInvestorFundDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MyInvestorFundsController implements MyInvestorFundsApi {

  private final MyInvestorFundDrivingPort myInvestorFundDrivingPort;
  private final MyInvestorFundApiMapper mapper;

  @Override
  public ResponseEntity<List<MyInvestorFund>> listMyInvestorFunds() {
    log.info("listMyInvestorFunds");
    List<MyInvestorFund> fondos = myInvestorFundDrivingPort.listar().stream()
        .map(mapper::toMyInvestorFundResponse)
        .toList();
    return ResponseEntity.ok(fondos);
  }

  @Override
  public ResponseEntity<MyInvestorFund> createMyInvestorFund(MyInvestorFundCreate myInvestorFundCreate) {
    log.info("createMyInvestorFund");
    FondoMyInvestor modeloEntrada = mapper.toDomain(myInvestorFundCreate);
    FondoMyInvestor modeloCreado = myInvestorFundDrivingPort.crear(modeloEntrada);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toMyInvestorFundResponse(modeloCreado));
  }

  @Override
  public ResponseEntity<MyInvestorFund> getMyInvestorFund(String id) {
    log.info("getMyInvestorFund id={}", id);
    FondoMyInvestor modelo = myInvestorFundDrivingPort.obtener(id);
    return ResponseEntity.ok(mapper.toMyInvestorFundResponse(modelo));
  }

  @Override
  public ResponseEntity<MyInvestorFund> updateMyInvestorFund(String id, MyInvestorFundUpdate myInvestorFundUpdate) {
    log.info("updateMyInvestorFund id={}", id);
    FondoMyInvestor modeloEntrada = mapper.toDomain(myInvestorFundUpdate);
    FondoMyInvestor modeloActualizado = myInvestorFundDrivingPort.actualizar(id, modeloEntrada);
    return ResponseEntity.ok(mapper.toMyInvestorFundResponse(modeloActualizado));
  }

  @Override
  public ResponseEntity<Void> deleteMyInvestorFund(String id) {
    log.info("deleteMyInvestorFund id={}", id);
    myInvestorFundDrivingPort.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}