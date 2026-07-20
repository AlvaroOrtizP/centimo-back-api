package com.centimo.api.database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "balances_fondo",
    uniqueConstraints = @UniqueConstraint(columnNames = {"fondo_id", "anio", "mes"}))
@Getter
@Setter
public class BalanceFondoMO {

  @Id
  @Column(length = 50)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fondo_id", nullable = false)
  private FondoMyInvestorMO fondo;

  @Column(name = "fondo_id", insertable = false, updatable = false)
  private String fondoId;

  @Column(nullable = false)
  private Integer anio;

  @Column(nullable = false)
  private Integer mes;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal saldo;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;
}
