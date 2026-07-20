package com.centimo.api.domain.models;

import com.centimo.api.domain.enums.EstadoProyecto;
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
public class InversionCrowdlending {

  private String id;
  private String plataformaId;
  private String nombreProyecto;
  private BigDecimal cantidadInvertida;
  private BigDecimal tipoInteres;
  private Integer plazoMeses;
  private LocalDate fechaInicio;
  private LocalDate fechaFin;
  private BigDecimal retornoMensual;
  private BigDecimal totalDevuelto;
  private EstadoProyecto estado;
  private LocalDateTime fechaCreacion;
  private LocalDateTime fechaActualizacion;
}
