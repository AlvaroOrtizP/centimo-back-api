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
public class InteresAnualMintos {

  private String id;
  private Integer anio;
  private BigDecimal cantidad;
  private BigDecimal retencionImpuestos;
  private BigDecimal tipoImpositivo;
  private BigDecimal importeNeto;
  private LocalDateTime fechaCreacion;
  private LocalDateTime fechaActualizacion;
}
