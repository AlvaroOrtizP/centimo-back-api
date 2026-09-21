package com.centimo.api.database.models;

import com.centimo.api.domain.enums.TipoSubcuentaB100;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "b100_balances")
@Getter
@Setter
public class B100BalanceMO {

  @Id
  @Column(length = 50)
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_subcuenta", nullable = false, length = 20)
  private TipoSubcuentaB100 tipoSubcuenta;

  @Column(name = "mes", nullable = false, length = 7)
  private String mes;

  @Column(name = "balance_mensual", nullable = false, precision = 12, scale = 2)
  private BigDecimal balanceMensual;

  @Column(name = "aporte_mensual", nullable = false, precision = 12, scale = 2)
  private BigDecimal aporteMensual = BigDecimal.ZERO;

  @Column(name = "dinero_hacienda", nullable = false, precision = 12, scale = 2)
  private BigDecimal dineroHacienda = BigDecimal.ZERO;

  @Column(name = "dinero_total_repartir", nullable = false, precision = 12, scale = 2)
  private BigDecimal dineroTotalRepartir;

  @Column(name = "porcentaje_hacienda", nullable = false, precision = 5, scale = 2)
  private BigDecimal porcentajeHacienda = new BigDecimal("19.00");

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;

  @UpdateTimestamp
  @Column(name = "fecha_actualizacion")
  private LocalDateTime fechaActualizacion;
}