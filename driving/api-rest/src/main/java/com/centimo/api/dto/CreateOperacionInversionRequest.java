package com.centimo.api.dto;

import com.centimo.api.domain.enums.EstadoOperacion;
import com.centimo.api.domain.enums.TipoActivo;
import com.centimo.api.domain.enums.TipoOperacion;
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
public class CreateOperacionInversionRequest {

  @NotBlank
  private String id;

  @NotBlank
  private String accountId;

  @NotBlank
  private String assetName;

  @NotNull
  private TipoActivo assetType;

  @NotNull
  private TipoOperacion type;

  @NotNull
  private LocalDate buyDate;

  @NotNull
  private BigDecimal buyQuantity;

  @NotNull
  private BigDecimal buyPricePerUnit;

  @NotNull
  private BigDecimal buyTotalCost;

  private LocalDate sellDate;

  private BigDecimal sellPricePerUnit;

  private BigDecimal sellTotalReceived;

  private BigDecimal sellQuantity;

  private BigDecimal pnl;

  @NotNull
  private EstadoOperacion status;
}
