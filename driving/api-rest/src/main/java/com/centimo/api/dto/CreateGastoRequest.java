package com.centimo.api.dto;

import com.centimo.api.domain.enums.CategoriaGasto;
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
public class CreateGastoRequest {

  @NotBlank
  private String id;

  @NotBlank
  private String snapshotId;

  @NotNull
  private CategoriaGasto category;

  @NotNull
  private BigDecimal amount;

  @NotNull
  private LocalDate date;

  private String description;
}
