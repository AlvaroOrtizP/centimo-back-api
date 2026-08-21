package com.centimo.api.database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "instantaneas_mensuales")
@Getter
@Setter
public class InstantaneaMensualMO {

  @Id
  @Column(length = 50)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cuenta_id", nullable = false)
  private CuentaMO cuenta;

  @Column(name = "cuenta_id", insertable = false, updatable = false)
  private String cuentaId;

  @Column(nullable = false)
  private Integer anio;

  @Column(nullable = false)
  private Integer mes;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal saldo = BigDecimal.ZERO;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal ingresos = BigDecimal.ZERO;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal gastos = BigDecimal.ZERO;

  @Column(precision = 10, scale = 2)
  private BigDecimal aportacion;

  @Column(precision = 10, scale = 2)
  private BigDecimal hacienda;

  @Column(columnDefinition = "TEXT")
  private String notas;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;

  @UpdateTimestamp
  @Column(name = "fecha_actualizacion")
  private LocalDateTime fechaActualizacion;
}
