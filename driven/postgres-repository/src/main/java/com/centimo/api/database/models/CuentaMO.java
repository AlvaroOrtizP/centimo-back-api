package com.centimo.api.database.models;

import com.centimo.api.domain.enums.TipoCuenta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cuentas")
@Getter
@Setter
public class CuentaMO {

  @Id
  @Column(length = 50)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plataforma_id", nullable = false)
  private PlataformaMO plataforma;

  @Column(name = "plataforma_id", insertable = false, updatable = false)
  private String plataformaId;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TipoCuenta tipo;

  @Column(nullable = false, length = 3)
  private String moneda = "EUR";

  @Column(nullable = false)
  private Integer orden;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;

  @UpdateTimestamp
  @Column(name = "fecha_actualizacion")
  private LocalDateTime fechaActualizacion;
}
