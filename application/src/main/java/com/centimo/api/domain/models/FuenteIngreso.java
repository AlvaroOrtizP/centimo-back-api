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
public class FuenteIngreso {

  private String id;
  private String instantaneaId;
  private String fuente;
  private String descripcion;
  private BigDecimal cantidad;
  private LocalDateTime fechaCreacion;
}
