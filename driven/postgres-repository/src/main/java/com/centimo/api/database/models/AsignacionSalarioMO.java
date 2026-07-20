package com.centimo.api.database.models;

import com.centimo.api.domain.enums.TipoAsignacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asignaciones_salario")
@Getter
@Setter
public class AsignacionSalarioMO {

  @Id
  @Column(length = 50)
  private String id;

  @Column(nullable = false)
  private Integer anio;

  @Column(nullable = false)
  private Integer mes;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plataforma_id", nullable = false)
  private PlataformaMO plataforma;

  @Column(name = "plataforma_id", insertable = false, updatable = false)
  private String plataformaId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TipoAsignacion tipo;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal valor;

  @Column(columnDefinition = "TEXT")
  private String nota;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;

  @UpdateTimestamp
  @Column(name = "fecha_actualizacion")
  private LocalDateTime fechaActualizacion;
}
