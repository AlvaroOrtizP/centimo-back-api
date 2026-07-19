package com.centimo.api.dto;

import com.centimo.api.domain.enums.TipoPlataforma;
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
public class UpdatePlataformaRequest {

  @NotBlank
  private String name;

  @NotNull
  private TipoPlataforma type;

  @NotBlank
  private String color;

  @NotBlank
  private String icon;

  @NotNull
  private Integer order;

  private String fixedNotes;
}
