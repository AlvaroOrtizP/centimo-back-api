package com.centimo.api.database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inversiones_crowdlending")
@Getter
@Setter
public class CrowdlendingInversionMO {

  @Id
  @Column(length = 50)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plataforma_id", nullable = false)
  private PlataformaMO plataforma;

  @Column(name = "plataforma_id", insertable = false, updatable = false)
  private String plataformaId;

  @Column(name = "nombre_proyecto", nullable = false, length = 200)
  private String nombreProyecto;

  @Column(name = "cantidad_invertida", nullable = false, precision = 10, scale = 2)
  private BigDecimal cantidadInvertida;

  @Column(name = "tipo_interes", nullable = false, precision = 5, scale = 2)
  private BigDecimal tipoInteres;

  @Column(name = "plazo_meses", nullable = false)
  private Integer plazoMeses;

  @Column(name = "fecha_inicio", nullable = false)
  private LocalDate fechaInicio;

  @Column(name = "fecha_fin")
  private LocalDate fechaFin;

  @Column(name = "retorno_mensual", nullable = false, precision = 10, scale = 2)
  private BigDecimal retornoMensual;

  @Column(name = "total_devuelto", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalDevuelto = BigDecimal.ZERO;

  @Column(nullable = false, length = 20)
  private String estado;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;

  @UpdateTimestamp
  @Column(name = "fecha_actualizacion")
  private LocalDateTime fechaActualizacion;
}
