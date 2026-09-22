package com.centimo.api.domain.models;

import com.centimo.api.domain.enums.EstadoEquitoCompra;
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
public class EquitoCompra {

  private String id;
  private LocalDate fecha;
  private String entidad;
  private BigDecimal monto;
  private BigDecimal rendimiento;
  private EstadoEquitoCompra estado;
  private LocalDateTime fechaCreacion;
  private LocalDateTime fechaActualizacion;
}