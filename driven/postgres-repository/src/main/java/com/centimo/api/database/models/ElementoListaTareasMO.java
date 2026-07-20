package com.centimo.api.database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "elementos_lista_tareas")
@Getter
@Setter
public class ElementoListaTareasMO {

  @Id
  @Column(length = 50)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "instantanea_id", nullable = false)
  private InstantaneaMensualMO instantanea;

  @Column(name = "instantanea_id", insertable = false, updatable = false)
  private String instantaneaId;

  @Column(nullable = false, length = 200)
  private String texto;

  @Column(nullable = false)
  private Boolean marcado = false;

  @Column(nullable = false)
  private Integer orden = 0;
}
