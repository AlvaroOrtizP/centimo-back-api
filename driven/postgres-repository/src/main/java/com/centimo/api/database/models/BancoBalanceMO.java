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
@Table(name = "banco_balances")
@Getter
@Setter
public class BancoBalanceMO {

  @Id
  @Column(length = 50)
  private String id;

  @Column(name = "entidad", nullable = false, length = 50)
  private String entidad;

  @Column(name = "mes", nullable = false, length = 7)
  private String mes;

  @Column(name = "balance_mensual", nullable = false, precision = 12, scale = 2)
  private BigDecimal balanceMensual;

  @Column(name = "aporte_mensual", nullable = false, precision = 12, scale = 2)
  private BigDecimal aporteMensual = BigDecimal.ZERO;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;

  @UpdateTimestamp
  @Column(name = "fecha_actualizacion")
  private LocalDateTime fechaActualizacion;
}