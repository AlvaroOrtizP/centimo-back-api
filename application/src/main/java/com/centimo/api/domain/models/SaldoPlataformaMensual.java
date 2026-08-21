package com.centimo.api.domain.models;

import com.centimo.api.domain.enums.TipoPlataforma;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaldoPlataformaMensual {

  private String plataformaId;
  private String nombrePlataforma;
  private TipoPlataforma tipo;
  private String color;
  private String icono;
  private Integer orden;
  private List<SaldoMensual> saldos;
}
