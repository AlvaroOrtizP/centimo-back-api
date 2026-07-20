package com.centimo.api.dto;

import jakarta.validation.constraints.NotNull;
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
public class UpdateInstantaneaRequest {

  @NotNull
  private BigDecimal balance;

  @NotNull
  private BigDecimal income;

  @NotNull
  private BigDecimal expenses;

  private BigDecimal contribution;

  private String notes;
}
