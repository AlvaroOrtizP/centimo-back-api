package com.centimo.api.dto;

import com.centimo.api.domain.enums.TipoCuenta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class UpdateCuentaRequest {

  @NotBlank
  private String platformId;

  @NotBlank
  private String name;

  @NotNull
  private TipoCuenta type;

  private String currency;

  @NotNull
  private Integer order;
}
