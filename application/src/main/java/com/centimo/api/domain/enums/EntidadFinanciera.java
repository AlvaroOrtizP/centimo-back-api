package com.centimo.api.domain.enums;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public enum EntidadFinanciera {
  REVOLUT("revolut", "Revolut", CategoriaEntidad.Liquidez, 1),
  BBVA("bbva", "BBVA", CategoriaEntidad.Liquidez, 9),
  CAIXABANK("caixabank", "CaixaBank", CategoriaEntidad.Liquidez, 10),
  B100("b100", "B100", CategoriaEntidad.Liquidez, 7),

  MINTOS("mintos", "Mintos", CategoriaEntidad.Fija, 4),
  EQUITO("equito", "Equito", CategoriaEntidad.Fija, 2),
  URBANITAE("urbanitae", "Urbanitae", CategoriaEntidad.Fija, 3),

  MYINVESTOR("myinvestor", "MyInvestor", CategoriaEntidad.Variable, 8),
  CRYPTO("crypto", "Cripto", CategoriaEntidad.Variable, 6),
  ACCIONES("acciones", "Acciones", CategoriaEntidad.Variable, 5),

  GASTOS("gastos", "Gastos", null, 11);

  private static final Set<EntidadFinanciera> BANCOS = EnumSet.of(BBVA, CAIXABANK);

  private final String codigo;
  private final String nombre;
  private final CategoriaEntidad categoria;
  private final int ordenTarjeta;

  EntidadFinanciera(String codigo, String nombre, CategoriaEntidad categoria, int ordenTarjeta) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.categoria = categoria;
    this.ordenTarjeta = ordenTarjeta;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public CategoriaEntidad getCategoria() {
    return categoria;
  }

  public int getOrdenTarjeta() {
    return ordenTarjeta;
  }

  public boolean esPatrimonio() {
    return categoria != null;
  }

  public boolean esBanco() {
    return BANCOS.contains(this);
  }

  public static Optional<EntidadFinanciera> porCodigo(String codigo) {
    return codigo == null
        ? Optional.empty()
        : Arrays.stream(values())
            .filter(entidad -> entidad.codigo.equalsIgnoreCase(codigo.trim()))
            .findFirst();
  }

  public static List<EntidadFinanciera> patrimonioOrdenado() {
    return Arrays.stream(values())
        .filter(EntidadFinanciera::esPatrimonio)
        .sorted((uno, otro) -> Integer.compare(uno.ordenTarjeta, otro.ordenTarjeta))
        .toList();
  }
}
