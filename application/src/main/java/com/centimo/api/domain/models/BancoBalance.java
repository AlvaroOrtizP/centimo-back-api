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
public class BancoBalance {

  private String id;
  private String entidad;
  private String mes;
  private BigDecimal balanceMensual;
  private BigDecimal aporteMensual;
  private LocalDateTime fechaCreacion;
  private LocalDateTime fechaActualizacion;
}