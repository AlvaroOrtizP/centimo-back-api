package com.centimo.api.domain.models;

import com.centimo.api.domain.enums.TipoAsignacion;
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
public class AsignacionSalario {

  private String id;
  private Integer anio;
  private Integer mes;
  private String plataformaId;
  private TipoAsignacion tipo;
  private BigDecimal valor;
  private String nota;
  private LocalDateTime fechaCreacion;
  private LocalDateTime fechaActualizacion;
}
