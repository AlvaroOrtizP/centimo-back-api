package com.centimo.api.domain.models;

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
public class Cuenta {

  private String id;
  private String plataformaId;
  private String nombre;
  private TipoCuenta tipo;
  private String moneda;
  private Integer orden;
}
