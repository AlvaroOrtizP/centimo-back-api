package com.centimo.api.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Gasto {

  private String id;
  private String instantaneaId;
  private String categoria;
  private BigDecimal cantidad;
  private LocalDate fecha;
  private String descripcion;
  private LocalDateTime fechaCreacion;
}
