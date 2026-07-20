package com.centimo.api.domain.models;

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
public class ElementoListaTareas {

  private String id;
  private String instantaneaId;
  private String texto;
  private Boolean marcado;
  private Integer orden;
}
