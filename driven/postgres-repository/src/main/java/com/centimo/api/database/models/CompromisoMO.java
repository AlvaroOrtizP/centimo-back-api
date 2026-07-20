package com.centimo.api.database.models;

import com.centimo.api.domain.enums.CategoriaCompromiso;
import com.centimo.api.domain.enums.TipoCompromiso;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "compromisos")
@Getter
@Setter
public class CompromisoMO {

  @Id
  @Column(length = 50)
  private String id;

  @Column(name = "descripcion", nullable = false, length = 200)
  private String descripcion;

  @Column(nullable = false)
  private Integer mes;

  @Column
  private Integer anio;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TipoCompromiso tipo;

  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  private CategoriaCompromiso categoria;

  @Column(precision = 10, scale = 2)
  private BigDecimal cantidad;

  @Column(name = "es_estimado")
  private Boolean esEstimado;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;

  @UpdateTimestamp
  @Column(name = "fecha_actualizacion")
  private LocalDateTime fechaActualizacion;
}
