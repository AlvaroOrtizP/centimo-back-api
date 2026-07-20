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
public class InstantaneaMensual {

  private String id;
  private String cuentaId;
  private Integer anio;
  private Integer mes;
  private BigDecimal saldo;
  private BigDecimal ingresos;
  private BigDecimal gastos;
  private BigDecimal aportacion;
  private String notas;
  private LocalDateTime fechaCreacion;
  private LocalDateTime fechaActualizacion;
}
