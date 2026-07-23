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
public class ResumenMensual {

  private Integer year;
  private Integer month;
  private BigDecimal totalBalance;
  private BigDecimal totalIncome;
  private BigDecimal totalExpenses;
  private BigDecimal balanceWithoutExpenses;
  private BigDecimal netWorth;
  private BigDecimal netSavings;
}
