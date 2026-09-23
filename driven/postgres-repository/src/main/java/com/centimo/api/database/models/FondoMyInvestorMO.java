package com.centimo.api.database.models;

import com.centimo.api.domain.enums.TipoActivoMyInvestor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

  @Column(name = "codigo_isin", length = 20, unique = true)
  private String codigoIsin;

  @Column(name = "nombre", nullable = false, length = 200)
  private String nombre;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo", nullable = false, length = 20)
  private TipoActivoMyInvestor tipo = TipoActivoMyInvestor.fondo;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;
}