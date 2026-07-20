package com.centimo.api.dto;

import com.centimo.api.domain.enums.CategoriaCompromiso;
import com.centimo.api.domain.enums.TipoCompromiso;
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
public class CreateCompromisoRequest {

  @NotBlank
  private String id;

  @NotBlank
  private String description;

  @NotNull
  private Integer month;

  private Integer year;

  @NotNull
  private TipoCompromiso type;

  private CategoriaCompromiso category;

  private BigDecimal amount;

  private Boolean isEstimated;
}
