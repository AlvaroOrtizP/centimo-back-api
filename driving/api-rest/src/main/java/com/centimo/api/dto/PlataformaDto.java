package com.centimo.api.dto;

import com.centimo.api.domain.enums.TipoPlataforma;
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
public class PlataformaDto {

  private String id;
  private String name;
  private TipoPlataforma type;
  private String color;
  private String icon;
  private Integer order;
  private String fixedNotes;
}
