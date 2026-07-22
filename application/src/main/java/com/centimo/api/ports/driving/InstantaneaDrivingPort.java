package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.InstantaneaMensual;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface InstantaneaDrivingPort {

    Optional<InstantaneaMensual> obtenerPorFecha(String accountId, Integer year, Integer month);

    @Transactional
    InstantaneaMensual crear(InstantaneaMensual nuevaInstantanea);
}
