package com.centimo.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class CreateBalanceFondoRequest {

  @NotBlank
  private String id;

  @NotBlank
  private String fundId;

  @NotNull
  @Min(2020)
  private Integer year;

  @NotNull
  @Min(1)
  private Integer month;

  @NotNull
  private BigDecimal balance;
}
