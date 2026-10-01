package com.centimo.api.domain.models;

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
public class DashboardCategoriaBalance {

  private String mes;
  private String codigo;
  private BigDecimal balance;
  private BigDecimal aporte;
}
