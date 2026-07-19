package com.mercadona.centimo.api.driven.jpa;

import com.mercadona.centimo.api.domain.enums.TipoPlataforma;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "plataformas")
@Getter
@Setter
public class Plataforma {

  @Id
  @NotBlank
  @Size(max = 50)
  @Column(length = 50)
  private String id;

  @NotBlank
  @Size(max = 100)
  @Column(nullable = false, length = 100)
  private String nombre;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TipoPlataforma tipo;

  @NotBlank
  @Pattern(regexp = "^#[0-9A-Fa-f]{6}$")
  @Column(nullable = false, length = 7)
  private String color;

  @NotBlank
  @Size(max = 50)
  @Column(nullable = false, length = 50)
  private String icono;

  @NotNull
  @Min(1)
  @Column(name = "orden", nullable = false)
  private Integer orden;

  @Column(columnDefinition = "TEXT")
  private String notasFijas;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;

  @UpdateTimestamp
  @Column(name = "fecha_actualizacion")
  private LocalDateTime fechaActualizacion;
}