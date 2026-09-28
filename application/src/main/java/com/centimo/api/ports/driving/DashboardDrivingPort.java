package com.centimo.api.ports.driving;

import com.centimo.api.domain.enums.CategoriaEntidad;
import com.centimo.api.domain.models.DashboardBalance;
import com.centimo.api.domain.models.DashboardCategoriaBalance;
import com.centimo.api.domain.models.DashboardSerieBalance;

import java.util.List;

public interface DashboardDrivingPort {

  DashboardBalance balanceDelMes(String mes);

  List<DashboardSerieBalance> serieDeEntidad(String entidad, String mes, Integer mesesAtras);

  List<DashboardSerieBalance> serieTotal(String mes, Integer mesesAtras);

  List<DashboardCategoriaBalance> seriePorCategoria(CategoriaEntidad categoria, String mes, Integer mesesAtras);
}
