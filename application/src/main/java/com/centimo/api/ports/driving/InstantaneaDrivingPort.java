package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.InstantaneaMensual;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InstantaneaDrivingPort {

    Optional<InstantaneaMensual> obtenerPorFecha(String accountId, Integer year, Integer month);

    List<InstantaneaMensual> listarTodas();

    @Transactional
    InstantaneaMensual crear(InstantaneaMensual nuevaInstantanea);

    @Transactional
    InstantaneaMensual upsert(String accountId, Integer year, Integer month,
                              BigDecimal balance, BigDecimal incomeDelta,
                              BigDecimal expenses, BigDecimal contribution);
}
