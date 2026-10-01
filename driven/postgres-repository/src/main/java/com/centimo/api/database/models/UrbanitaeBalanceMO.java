package com.centimo.api.database.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "urbanitae_balances")
@Getter
@Setter
public class UrbanitaeBalanceMO {

  @Id
  @Column(length = 50)
  private String id;

  @Column(name = "mes", nullable = false, length = 7)
  private String mes;

  @Column(name = "balance_mensual", nullable = false, precision = 12, scale = 2)
  private BigDecimal balanceMensual;

  @Column(name = "aporte_mensual", nullable = false, precision = 12, scale = 2)
  private BigDecimal aporteMensual = BigDecimal.ZERO;

  @Column(name = "dinero_total", nullable = false, precision = 12, scale = 2)
  private BigDecimal dineroTotal = BigDecimal.ZERO;

  @Column(name = "dinero_hacienda", nullable = false, precision = 12, scale = 2)
  private BigDecimal dineroHacienda = BigDecimal.ZERO;

  @Column(name = "dinero_final", nullable = false, precision = 12, scale = 2)
  private BigDecimal dineroFinal = BigDecimal.ZERO;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;

  @UpdateTimestamp
  @Column(name = "fecha_actualizacion")
  private LocalDateTime fechaActualizacion;
}