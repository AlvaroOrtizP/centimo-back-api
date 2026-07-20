package com.centimo.api.database.models;

import com.centimo.api.domain.enums.TipoActivo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "posiciones_inversion")
@Getter
@Setter
public class PosicionInversionMO {

  @Id
  @Column(length = 50)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "instantanea_id", nullable = false)
  private InstantaneaMensualMO instantanea;

  @Column(name = "instantanea_id", insertable = false, updatable = false)
  private String instantaneaId;

  @Column(name = "nombre_activo", nullable = false, length = 100)
  private String nombreActivo;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_activo", nullable = false, length = 20)
  private TipoActivo tipoActivo;

  @Column(nullable = false, precision = 18, scale = 8)
  private BigDecimal cantidad;

  @Column(name = "valor_unitario", nullable = false, precision = 12, scale = 4)
  private BigDecimal valorUnitario;

  @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
  private BigDecimal valorTotal;

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false)
  private LocalDateTime fechaCreacion;
}
