package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.InstantaneaMensual;

public interface InstantaneaDrivingPort {

    InstantaneaMensual obtenerPorFecha(Integer year, Integer month);
}
