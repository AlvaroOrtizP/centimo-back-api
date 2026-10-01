package com.centimo.api.domain.enums;

import java.util.Arrays;
import java.util.List;

public enum CategoriaEntidad {
  Liquidez,
  Fija,
  Variable,
  Todas;

  public List<EntidadFinanciera> entidades() {
    return switch (this) {
      case Todas -> Arrays.stream(EntidadFinanciera.values())
          .filter(entidad -> entidad.getCategoria() != null)
          .toList();
      default -> Arrays.stream(EntidadFinanciera.values())
          .filter(entidad -> entidad.getCategoria() == this)
          .toList();
    };
  }
}
