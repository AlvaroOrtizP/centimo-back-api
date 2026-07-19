package com.centimo.api.dto;

import com.centimo.api.domain.enums.TipoCuenta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaDto {

  private String id;
  private String platformId;
  private String name;
  private TipoCuenta type;
  private String currency;
  private Integer order;
}
