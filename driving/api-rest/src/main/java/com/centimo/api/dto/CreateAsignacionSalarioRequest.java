package com.centimo.api.dto;

import com.centimo.api.domain.enums.TipoAsignacion;
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
public class CreateAsignacionSalarioRequest {

  @NotBlank
  private String id;

  @NotNull
  private Integer year;

  @NotNull
  private Integer month;

  @NotBlank
  private String platformId;

  @NotNull
  private TipoAsignacion type;

  @NotNull
  private BigDecimal value;

  private String note;
}
