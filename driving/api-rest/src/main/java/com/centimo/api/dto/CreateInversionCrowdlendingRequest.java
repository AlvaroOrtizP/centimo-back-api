package com.centimo.api.dto;

import com.centimo.api.domain.enums.EstadoProyecto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateInversionCrowdlendingRequest {

  @NotBlank
  private String id;

  @NotBlank
  private String platformId;

  @NotBlank
  private String projectName;

  @NotNull
  private BigDecimal investedAmount;

  @NotNull
  private BigDecimal interestRate;

  @NotNull
  private Integer termMonths;

  @NotNull
  private LocalDate startDate;

  private LocalDate endDate;

  @NotNull
  private BigDecimal monthlyReturn;

  @NotNull
  private BigDecimal totalReturned;

  @NotNull
  private EstadoProyecto status;
}
