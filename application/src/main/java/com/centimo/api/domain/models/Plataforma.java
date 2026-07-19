package com.centimo.api.domain.models;

import com.centimo.api.domain.enums.TipoPlataforma;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plataforma {

  private String id;
  private String nombre;
  private TipoPlataforma tipo;
  private String color;
  private String icono;
  private Integer orden;
  private String notasFijas;
  private LocalDateTime fechaCreacion;
  private LocalDateTime fechaActualizacion;
}
