package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.ResumenMensual;
import com.centimo.api.domain.models.SaldoPlataformaMensual;

import java.util.List;

public interface SummariesDrivingPort {

  ResumenMensual obtenerResumenMensual(Integer year, Integer month);

  List<ResumenMensual> obtenerResumenesMensuales(Integer year, Integer month, Integer months);

  List<SaldoPlataformaMensual> obtenerSaldosPlataformasMensuales(Integer year, Integer month, Integer months);
}
