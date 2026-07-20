package com.centimo.api.dto;

import com.centimo.api.domain.enums.CategoriaGasto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GastoDto {

  private String id;
  private String snapshotId;
  private CategoriaGasto category;
  private BigDecimal amount;
  private LocalDate date;
  private String description;
}
