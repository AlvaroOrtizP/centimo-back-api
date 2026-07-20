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
public class BalanceFondoDto {

  private String id;
  private String fundId;
  private Integer year;
  private Integer month;
  private BigDecimal balance;
}
