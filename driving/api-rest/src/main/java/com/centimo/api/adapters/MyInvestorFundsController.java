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
    List<MyInvestorFund> fondos = myInvestorFundDrivingPort.listAll()
            .stream()
            .map(mapper::toMyInvestorFund)
            .toList();
    return ResponseEntity.ok(fondos);
  }

  @Override
  public ResponseEntity<MyInvestorFund> getMyInvestorFund(String id) {
    log.info("getMyInvestorFund id={}", id);
    FondoMyInvestor fondo = myInvestorFundDrivingPort.getById(id);
    return ResponseEntity.ok(mapper.toMyInvestorFund(fondo));
  }

  @Override
  public ResponseEntity<MyInvestorFund> createMyInvestorFund(MyInvestorFundCreate myInvestorFundCreate) {
    log.info("createMyInvestorFund");
    FondoMyInvestor modeloEntrada = mapper.toDomain(myInvestorFundCreate);
    FondoMyInvestor modeloCreado = myInvestorFundDrivingPort.create(modeloEntrada);
    MyInvestorFund response = mapper.toMyInvestorFund(modeloCreado);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Override
  public ResponseEntity<MyInvestorFund> updateMyInvestorFund(String id, MyInvestorFundUpdate myInvestorFundUpdate) {
    log.info("updateMyInvestorFund id={}", id);
    FondoMyInvestor modeloEntrada = mapper.toDomain(myInvestorFundUpdate);
    FondoMyInvestor modeloActualizado = myInvestorFundDrivingPort.update(id, modeloEntrada);
    MyInvestorFund response = mapper.toMyInvestorFund(modeloActualizado);
    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<Void> deleteMyInvestorFund(String id) {
    log.info("deleteMyInvestorFund id={}", id);
    myInvestorFundDrivingPort.delete(id);
    return ResponseEntity.noContent().build();
  }
}
