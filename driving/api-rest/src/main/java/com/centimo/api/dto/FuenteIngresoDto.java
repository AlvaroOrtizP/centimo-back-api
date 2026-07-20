package com.centimo.api.dto;

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
public class FuenteIngresoDto {

  private String id;
  private String snapshotId;
  private String source;
  private String description;
  private BigDecimal amount;
}
