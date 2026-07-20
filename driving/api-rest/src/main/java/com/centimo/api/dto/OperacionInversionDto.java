package com.centimo.api.dto;

import com.centimo.api.domain.enums.EstadoOperacion;
import com.centimo.api.domain.enums.TipoActivo;
import com.centimo.api.domain.enums.TipoOperacion;
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
public class OperacionInversionDto {

  private String id;
  private String accountId;
  private String assetName;
  private TipoActivo assetType;
  private TipoOperacion type;
  private LocalDate buyDate;
  private BigDecimal buyQuantity;
  private BigDecimal buyPricePerUnit;
  private BigDecimal buyTotalCost;
  private LocalDate sellDate;
  private BigDecimal sellPricePerUnit;
  private BigDecimal sellTotalReceived;
  private BigDecimal sellQuantity;
  private BigDecimal pnl;
  private EstadoOperacion status;
}
