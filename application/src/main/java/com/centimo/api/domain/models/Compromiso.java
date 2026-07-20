package com.centimo.api.domain.models;

import com.centimo.api.domain.enums.CategoriaCompromiso;
import com.centimo.api.domain.enums.TipoCompromiso;
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
public class Compromiso {

  private String id;
  private String descripcion;
  private Integer mes;
  private Integer anio;
  private TipoCompromiso tipo;
  private CategoriaCompromiso categoria;
  private BigDecimal cantidad;
  private Boolean esEstimado;
  private LocalDateTime fechaCreacion;
  private LocalDateTime fechaActualizacion;
}
