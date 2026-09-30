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
public class Nomina {

  private String mes;
  private BigDecimal cantidad;
  private String nota;
  private LocalDateTime fechaCreacion;
  private LocalDateTime fechaActualizacion;
}