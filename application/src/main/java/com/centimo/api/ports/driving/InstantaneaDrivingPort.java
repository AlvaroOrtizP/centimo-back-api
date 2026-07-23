package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.InstantaneaMensual;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface InstantaneaDrivingPort {

    Optional<InstantaneaMensual> obtenerPorFecha(String accountId, Integer year, Integer month);

    List<InstantaneaMensual> listarTodas();

    @Transactional
    InstantaneaMensual crear(InstantaneaMensual nuevaInstantanea);

    @Transactional
    InstantaneaMensual upsert(String accountId, Integer year, Integer month,
                              java.math.BigDecimal balance, java.math.BigDecimal incomeDelta,
                              java.math.BigDecimal expenses, java.math.BigDecimal contribution);
}
