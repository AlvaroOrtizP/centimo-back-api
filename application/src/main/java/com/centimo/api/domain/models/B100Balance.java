package com.centimo.api.domain.models;

import com.centimo.api.domain.enums.TipoSubcuentaB100;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B100Balance {

  private String id;
  private TipoSubcuentaB100 tipoSubcuenta;
  private String mes;
  private BigDecimal balanceMensual;
  private BigDecimal aporteMensual;
  private BigDecimal dineroHacienda;
  private BigDecimal dineroTotalRepartir;
  private BigDecimal porcentajeHacienda;
  private LocalDateTime fechaCreacion;
  private LocalDateTime fechaActualizacion;
}