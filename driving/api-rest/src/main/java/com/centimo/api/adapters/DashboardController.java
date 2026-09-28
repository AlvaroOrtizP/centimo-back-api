package com.centimo.api.adapters;

import com.centimo.api.DashboardApi;
import com.centimo.api.dto.DashboardCategoria;
import com.centimo.api.dto.DashboardCategoriaBalance;
import com.centimo.api.dto.DashboardResponse;
import com.centimo.api.dto.DashboardSerieBalance;
import com.centimo.api.mappers.DashboardApiMapper;
import com.centimo.api.ports.driving.DashboardDrivingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DashboardController implements DashboardApi {

  private final DashboardDrivingPort dashboardDrivingPort;
  private final DashboardApiMapper mapper;

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(DashboardCategoria.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) {
        setValue(Arrays.stream(DashboardCategoria.values())
            .filter(categoria -> categoria.getValue().equalsIgnoreCase(text))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Categoría desconocida: " + text)));
      }
    });
  }

  @Override
  public ResponseEntity<DashboardResponse> getDashboardBalance(String mes) {
    log.info("getDashboardBalance mes={}", mes);
    return ResponseEntity.ok(mapper.toDashboardResponse(dashboardDrivingPort.balanceDelMes(mes)));
  }

  @Override
  public ResponseEntity<List<DashboardSerieBalance>> listDashboardBalanceSerie(String mes, String entidad, Integer mesesAtras) {
    log.info("listDashboardBalanceSerie mes={} entidad={} mesesAtras={}", mes, entidad, mesesAtras);
    List<DashboardSerieBalance> serie = entidad == null || entidad.isBlank()
        ? mapper.toDashboardSerieResponse(dashboardDrivingPort.serieTotal(mes, mesesAtras))
        : mapper.toDashboardSerieResponse(dashboardDrivingPort.serieDeEntidad(entidad, mes, mesesAtras));
    return ResponseEntity.ok(serie);
  }

  @Override
  public ResponseEntity<List<DashboardCategoriaBalance>> listDashboardCategoriaSerie(DashboardCategoria categoria, String mes, Integer mesesAtras) {
    log.info("listDashboardCategoriaSerie categoria={} mes={} mesesAtras={}", categoria, mes, mesesAtras);
    return ResponseEntity.ok(mapper.toDashboardCategoriaResponse(
        dashboardDrivingPort.seriePorCategoria(convertir(categoria), mes, mesesAtras)));
  }

  private com.centimo.api.domain.enums.CategoriaEntidad convertir(DashboardCategoria categoria) {
    if (categoria == null) {
      return null;
    }
    return com.centimo.api.domain.enums.CategoriaEntidad.valueOf(categoria.getValue());
  }
}