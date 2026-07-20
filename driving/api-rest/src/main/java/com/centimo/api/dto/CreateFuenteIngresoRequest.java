package com.centimo.api.dto;

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
public class CreateFuenteIngresoRequest {

  @NotBlank
  private String id;

  @NotBlank
  private String snapshotId;

  @NotBlank
  private String source;

  @NotBlank
  private String description;

  @NotNull
  private BigDecimal amount;
}
