package com.centimo.api.database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gastos")
@Getter
@Setter
public class GastoMO {

  @Id
  @Column(length = 50)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "instantanea_id", nullable = false)
  private InstantaneaMensualMO instantanea;

  @Column(name = "instantanea_id", insertable = false, updatable = false)
  private String instantaneaId;

  @Column(nullable = false, length = 20)
  private String categoria;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal cantidad;

  @Column(nullable = false)
  private LocalDate fecha;

  @Column(columnDefinition = "TEXT")
  private String descripcion;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;
}
