package com.centimo.api.domain.models;

import com.centimo.api.domain.enums.TipoActivoMyInvestor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FondoMyInvestor {

  private String id;
  private String codigoIsin;
  private String nombre;
  private TipoActivoMyInvestor tipo;
  private LocalDateTime fechaCreacion;
}