package com.centimo.api.database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fuentes_ingreso")
@Getter
@Setter
public class FuenteIngresoMO {

  @Id
  @Column(length = 50)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "instantanea_id", nullable = false)
  private InstantaneaMensualMO instantanea;

  @Column(name = "instantanea_id", insertable = false, updatable = false)
  private String instantaneaId;

  @Column(nullable = false, length = 50)
  private String fuente;

  @Column(nullable = false, length = 200)
  private String descripcion;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal cantidad;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;
}
