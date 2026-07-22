package com.centimo.api.usecases;

import com.centimo.api.domain.exceptions.NotFoundException;
import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.ports.driven.InstantaneaDrivenPort;
import com.centimo.api.ports.driving.InstantaneaDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstantaneaUseCase implements InstantaneaDrivingPort {

  private final InstantaneaDrivenPort instantaneaDrivenPort;

  @Override
  public InstantaneaMensual obtenerPorFecha(Integer year, Integer month) {
    return instantaneaDrivenPort.findByAnioAndMes(year, month);
  }
}
