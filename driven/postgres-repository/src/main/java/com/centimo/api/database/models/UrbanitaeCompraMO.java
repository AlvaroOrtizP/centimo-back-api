package com.centimo.api.database.models;

import com.centimo.api.domain.enums.EstadoUrbanitaeCompra;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "urbanitae_compras")
@Getter
@Setter
public class UrbanitaeCompraMO {

  @Id
  @Column(length = 50)
  private String id;

  @Column(name = "fecha", nullable = false)
  private LocalDate fecha;

  @Column(name = "entidad", nullable = false, length = 100)
  private String entidad;

  @Column(name = "monto", nullable = false, precision = 12, scale = 2)
  private BigDecimal monto;

  @Column(name = "rendimiento", nullable = false, precision = 5, scale = 2)
  private BigDecimal rendimiento = BigDecimal.ZERO;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false, length = 20)
  private EstadoUrbanitaeCompra estado;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;

  @UpdateTimestamp
  @Column(name = "fecha_actualizacion")
  private LocalDateTime fechaActualizacion;
}