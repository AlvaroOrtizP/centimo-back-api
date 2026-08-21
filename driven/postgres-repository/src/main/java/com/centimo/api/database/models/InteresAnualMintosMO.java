package com.centimo.api.database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mintos_intereses_anuales")
@Getter
@Setter
public class InteresAnualMintosMO {

  @Id
  @Column(length = 50)
  private String id;

  @Column(name = "anio", nullable = false, unique = true)
  private Integer anio;

  @Column(name = "cantidad", nullable = false, precision = 12, scale = 2)
  private BigDecimal cantidad;

  @Column(name = "retencion_impuestos", nullable = false, precision = 12, scale = 2)
  private BigDecimal retencionImpuestos;

  @Column(name = "tipo_impositivo", nullable = false, precision = 5, scale = 2)
  private BigDecimal tipoImpositivo;

  @Column(name = "importe_neto", nullable = false, precision = 12, scale = 2)
  private BigDecimal importeNeto;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;

  @UpdateTimestamp
  @Column(name = "fecha_actualizacion")
  private LocalDateTime fechaActualizacion;
}
