package com.centimo.api.dto;

import com.centimo.api.domain.enums.CategoriaCompromiso;
import com.centimo.api.domain.enums.TipoCompromiso;
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
public class CompromisoDto {

  private String id;
  private String description;
  private Integer month;
  private Integer year;
  private TipoCompromiso type;
  private CategoriaCompromiso category;
  private BigDecimal amount;
  private Boolean isEstimated;
}
