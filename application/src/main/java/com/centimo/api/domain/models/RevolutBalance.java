package com.centimo.api.domain.models;

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
public class RevolutBalance {

  private String id;
  private String mes;
  private BigDecimal balanceMensual;
  private BigDecimal aporteMensual;
  private BigDecimal dineroTotal;
  private BigDecimal dineroHacienda;
  private BigDecimal dineroFinal;
  private LocalDateTime fechaCreacion;
  private LocalDateTime fechaActualizacion;
}
