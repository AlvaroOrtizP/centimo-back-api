package com.centimo.api.database.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "balances_fondo")
@Getter
@Setter
public class BalanceFondoMO {

  @Id
  @Column(length = 50)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fondo_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private FondoMyInvestorMO fondo;

  @Column(name = "fondo_id", insertable = false, updatable = false)
  private String fondoId;

  @Column(name = "anio", nullable = false)
  private Integer anio;

  @Column(name = "mes", nullable = false)
  private Integer mes;

  @Column(name = "saldo", nullable = false, precision = 12, scale = 2)
  private BigDecimal saldo;

  @Column(name = "intereses", precision = 12, scale = 2)
  private BigDecimal intereses;

  @Column(name = "aportacion", precision = 12, scale = 2)
  private BigDecimal aportacion;

  @Column(name = "retirada", precision = 12, scale = 2)
  private BigDecimal retirada;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;
}