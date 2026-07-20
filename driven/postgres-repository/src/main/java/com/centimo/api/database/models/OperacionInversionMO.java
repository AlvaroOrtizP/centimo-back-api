package com.centimo.api.database.models;

import com.centimo.api.domain.enums.EstadoOperacion;
import com.centimo.api.domain.enums.TipoActivo;
import com.centimo.api.domain.enums.TipoOperacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "operaciones_inversion")
@Getter
@Setter
public class OperacionInversionMO {

  @Id
  @Column(length = 50)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cuenta_id", nullable = false)
  private CuentaMO cuenta;

  @Column(name = "cuenta_id", insertable = false, updatable = false)
  private String cuentaId;

  @Column(name = "nombre_activo", nullable = false, length = 100)
  private String nombreActivo;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_activo", nullable = false, length = 20)
  private TipoActivo tipoActivo;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private TipoOperacion tipo;

  @Column(name = "fecha_compra", nullable = false)
  private LocalDate fechaCompra;

  @Column(name = "cantidad_compra", nullable = false, precision = 18, scale = 8)
  private BigDecimal cantidadCompra;

  @Column(name = "precio_unitario_compra", nullable = false, precision = 12, scale = 4)
  private BigDecimal precioUnitarioCompra;

  @Column(name = "coste_total_compra", nullable = false, precision = 12, scale = 2)
  private BigDecimal costeTotalCompra;

  @Column(name = "fecha_venta")
  private LocalDate fechaVenta;

  @Column(name = "precio_unitario_venta", precision = 12, scale = 4)
  private BigDecimal precioUnitarioVenta;

  @Column(name = "cantidad_total_recibida", precision = 12, scale = 2)
  private BigDecimal cantidadTotalRecibida;

  @Column(name = "cantidad_venta", precision = 18, scale = 8)
  private BigDecimal cantidadVenta;

  @Column(name = "ganancia_perdida", precision = 12, scale = 2)
  private BigDecimal gananciaPerdida;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private EstadoOperacion estado;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;
}
