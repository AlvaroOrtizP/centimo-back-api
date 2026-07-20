package com.centimo.api.dto;

import com.centimo.api.domain.enums.TipoAsignacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionSalarioDto {

  private String id;
  private Integer year;
  private Integer month;
  private String platformId;
  private TipoAsignacion type;
  private BigDecimal value;
  private String note;
}
