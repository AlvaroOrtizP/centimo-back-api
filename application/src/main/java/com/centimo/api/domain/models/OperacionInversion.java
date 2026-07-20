package com.centimo.api.domain.models;

import com.centimo.api.domain.enums.EstadoOperacion;
import com.centimo.api.domain.enums.TipoActivo;
import com.centimo.api.domain.enums.TipoOperacion;
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
public class OperacionInversion {

  private String id;
  private String cuentaId;
  private String nombreActivo;
  private TipoActivo tipoActivo;
  private TipoOperacion tipo;

  private LocalDate fechaCompra;
  private BigDecimal cantidadCompra;
  private BigDecimal precioUnitarioCompra;
  private BigDecimal costeTotalCompra;

  private LocalDate fechaVenta;
  private BigDecimal precioUnitarioVenta;
  private BigDecimal cantidadTotalRecibida;
  private BigDecimal cantidadVenta;

  private BigDecimal gananciaPerdida;
  private EstadoOperacion estado;

  private LocalDateTime fechaCreacion;
}
