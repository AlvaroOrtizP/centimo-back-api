package com.centimo.api.dto;

import com.centimo.api.domain.enums.TipoActivo;
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
public class CreatePosicionInversionRequest {

  @NotBlank
  private String id;

  @NotBlank
  private String snapshotId;

  @NotBlank
  private String assetName;

  @NotNull
  private TipoActivo assetType;

  @NotNull
  private BigDecimal quantity;

  @NotNull
  private BigDecimal valuePerUnit;

  @NotNull
  private BigDecimal totalValue;
}
