package com.centimo.api.database.models;

import com.centimo.api.domain.enums.ExpenseCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

  @Enumerated(EnumType.STRING)
  @Column(name = "categoria", nullable = false, length = 20)
  private ExpenseCategory categoria;

  @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
  private BigDecimal cantidad;

  @Column(name = "fecha", nullable = false)
  private LocalDate fecha;

  @Column(columnDefinition = "TEXT")
  private String descripcion;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;
}