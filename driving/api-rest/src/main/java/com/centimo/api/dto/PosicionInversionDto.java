package com.centimo.api.dto;

import com.centimo.api.domain.enums.TipoActivo;
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
public class PosicionInversionDto {

  private String id;
  private String snapshotId;
  private String assetName;
  private TipoActivo assetType;
  private BigDecimal quantity;
  private BigDecimal valuePerUnit;
  private BigDecimal totalValue;
}
