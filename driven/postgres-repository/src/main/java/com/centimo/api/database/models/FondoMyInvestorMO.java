package com.centimo.api.database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fondos_myinvestor")
@Getter
@Setter
public class FondoMyInvestorMO {

  @Id
  @Column(length = 50)
  private String id;

  @Column(name = "codigo_isin", nullable = false, unique = true, length = 20)
  private String codigoIsin;

  @Column(nullable = false, length = 200)
  private String nombre;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;
}
