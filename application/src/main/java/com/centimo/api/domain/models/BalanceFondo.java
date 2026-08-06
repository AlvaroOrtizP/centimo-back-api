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
public class BalanceFondo {

  private String id;
  private String fondoId;
  private Integer anio;
  private Integer mes;
  private BigDecimal saldo;
  private BigDecimal intereses;
  private BigDecimal aportacion;
  private BigDecimal retirada;
  private LocalDateTime fechaCreacion;
}
