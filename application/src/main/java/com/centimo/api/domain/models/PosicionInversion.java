package com.centimo.api.domain.models;

import com.centimo.api.domain.enums.TipoActivo;
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
public class PosicionInversion {

  private String id;
  private String instantaneaId;
  private String nombreActivo;
  private TipoActivo tipoActivo;
  private BigDecimal cantidad;
  private BigDecimal valorUnitario;
  private BigDecimal valorTotal;
  private LocalDateTime fechaCreacion;
}
