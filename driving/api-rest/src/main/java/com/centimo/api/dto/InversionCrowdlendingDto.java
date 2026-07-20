package com.centimo.api.dto;

import com.centimo.api.domain.enums.EstadoProyecto;
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
public class InversionCrowdlendingDto {

  private String id;
  private String platformId;
  private String projectName;
  private BigDecimal investedAmount;
  private BigDecimal interestRate;
  private Integer termMonths;
  private LocalDate startDate;
  private LocalDate endDate;
  private BigDecimal monthlyReturn;
  private BigDecimal totalReturned;
  private EstadoProyecto status;
}
