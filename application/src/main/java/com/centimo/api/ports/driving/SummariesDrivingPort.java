package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.ResumenMensual;

public interface SummariesDrivingPort {

  ResumenMensual obtenerResumenMensual(Integer year, Integer month);
}
