package com.centimo.api.usecases;

import com.centimo.api.domain.enums.CategoriaEntidad;
import com.centimo.api.domain.enums.EntidadFinanciera;
import com.centimo.api.domain.enums.TipoSubcuentaB100;
import com.centimo.api.domain.exception.InvalidRequestException;
import com.centimo.api.domain.models.DashboardBalance;
import com.centimo.api.domain.models.DashboardCategoriaBalance;
import com.centimo.api.domain.models.DashboardEntityBalance;
import com.centimo.api.domain.models.DashboardSerieBalance;
import com.centimo.api.domain.models.Gasto;
import com.centimo.api.ports.driven.B100BalanceDrivenPort;
import com.centimo.api.ports.driven.BancoBalanceDrivenPort;
import com.centimo.api.ports.driven.EquitoBalanceDrivenPort;
import com.centimo.api.ports.driven.FundBalanceDrivenPort;
import com.centimo.api.ports.driven.GastoDrivenPort;
import com.centimo.api.ports.driven.InteresAnualMintosDrivenPort;
import com.centimo.api.ports.driven.RevolutBalanceDrivenPort;
import com.centimo.api.ports.driven.UrbanitaeBalanceDrivenPort;
import com.centimo.api.ports.driving.DashboardDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DashboardUseCase implements DashboardDrivingPort {

  static final int MESES_ATRAS_POR_DEFECTO = 6;
  static final int MESES_ATRAS_MAXIMO = 24;

  private final RevolutBalanceDrivenPort revolutBalanceDrivenPort;
  private final EquitoBalanceDrivenPort equitoBalanceDrivenPort;
  private final UrbanitaeBalanceDrivenPort urbanitaeBalanceDrivenPort;
  private final InteresAnualMintosDrivenPort interesAnualMintosDrivenPort;
  private final B100BalanceDrivenPort b100BalanceDrivenPort;
  private final BancoBalanceDrivenPort bancoBalanceDrivenPort;
  private final FundBalanceDrivenPort fundBalanceDrivenPort;
  private final GastoDrivenPort gastoDrivenPort;

  @Override
  public DashboardBalance balanceDelMes(String mes) {
    YearMonth ancla = parsearMes(mes);
    List<YearMonth> rango = List.of(ancla);
    Map<String, Map<YearMonth, Importes>> bancos = importesDeBancos(entidadesBancoConocidas(), rango);

    List<DashboardEntityBalance> entidades = new ArrayList<>();
    for (EntidadFinanciera entidad : EntidadFinanciera.patrimonioOrdenado()) {
      Importes importes = importesDe(entidad, rango, bancos).get(ancla);
      if (importes.tieneDatos()) {
        entidades.add(filaEntidad(entidad.getCodigo(), entidad.getNombre(), importes));
      }
    }
    bancos.forEach((codigo, porMes) -> {
      if (EntidadFinanciera.porCodigo(codigo).isPresent()) {
        return;
      }
      Importes importes = porMes.get(ancla);
      if (importes.tieneDatos()) {
        entidades.add(filaEntidad(codigo, codigo, importes));
      }
    });

    BigDecimal total = entidades.stream()
        .map(DashboardEntityBalance::getBalance)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return DashboardBalance.builder()
        .mes(ancla.toString())
        .total(total)
        .entidades(entidades)
        .build();
  }

  @Override
  public List<DashboardSerieBalance> serieDeEntidad(String entidad, String mes, Integer mesesAtras) {
    YearMonth ancla = parsearMes(mes);
    List<YearMonth> rango = mesesDelRango(ancla, mesesAtras);
    Resolucion resolucion = resolverEntidad(entidad);

    Map<YearMonth, Importes> porMes = resolucion.esBanco()
        ? importesDeBancos(List.of(resolucion.codigo()), rango).get(resolucion.codigo())
        : importesDe(EntidadFinanciera.valueOf(resolucion.entidad()), rango, Map.of());

    return rango.stream()
        .map(mesDelRango -> {
          Importes importes = porMes.getOrDefault(mesDelRango, Importes.VACIO);
          return DashboardSerieBalance.builder()
              .mes(mesDelRango.toString())
              .balance(importes.balance())
              .aporte(importes.aporte())
              .build();
        })
        .toList();
  }

  @Override
  public List<DashboardSerieBalance> serieTotal(String mes, Integer mesesAtras) {
    YearMonth ancla = parsearMes(mes);
    List<YearMonth> rango = mesesDelRango(ancla, mesesAtras);
    Map<String, Map<YearMonth, Importes>> bancos = importesDeBancos(entidadesBancoConocidas(), rango);

    Map<YearMonth, Importes> agregado = vacio(rango);
    for (EntidadFinanciera entidad : EntidadFinanciera.patrimonioOrdenado()) {
      importesDe(entidad, rango, bancos).forEach(
          (mesDelRango, importes) -> acumular(agregado, mesDelRango, importes.balance(), importes.aporte()));
    }
    bancos.forEach((codigo, porMes) -> {
      if (EntidadFinanciera.porCodigo(codigo).isPresent()) {
        return;
      }
      porMes.forEach(
          (mesDelRango, importes) -> acumular(agregado, mesDelRango, importes.balance(), importes.aporte()));
    });

    return rango.stream()
        .map(mesDelRango -> DashboardSerieBalance.builder()
            .mes(mesDelRango.toString())
            .balance(agregado.get(mesDelRango).balance())
            .aporte(agregado.get(mesDelRango).aporte())
            .build())
        .toList();
  }

  @Override
  public List<DashboardCategoriaBalance> seriePorCategoria(CategoriaEntidad categoria, String mes, Integer mesesAtras) {
    if (categoria == null) {
      throw new InvalidRequestException("La categoría es obligatoria");
    }
    YearMonth ancla = parsearMes(mes);
    List<YearMonth> rango = mesesDelRango(ancla, mesesAtras);
    List<EntidadFinanciera> entidades = categoria.entidades();

    Map<String, Map<YearMonth, Importes>> bancos = importesDeBancos(
        entidades.stream().filter(EntidadFinanciera::esBanco).map(EntidadFinanciera::getCodigo).toList(),
        rango);
    Map<EntidadFinanciera, Map<YearMonth, Importes>> porEntidad = entidades.stream()
        .collect(Collectors.toMap(
            Function.identity(),
            entidad -> importesDe(entidad, rango, bancos),
            (primero, segundo) -> primero,
            LinkedHashMap::new));

    List<DashboardCategoriaBalance> filas = new ArrayList<>();
    for (YearMonth mesDelRango : rango) {
      for (EntidadFinanciera entidad : entidades) {
        Importes importes = porEntidad.get(entidad).getOrDefault(mesDelRango, Importes.VACIO);
        filas.add(DashboardCategoriaBalance.builder()
            .mes(mesDelRango.toString())
            .codigo(entidad.getCodigo())
            .balance(importes.balance())
            .aporte(importes.aporte())
            .build());
      }
    }
    return filas;
  }

  private Map<YearMonth, Importes> importesDe(EntidadFinanciera entidad, List<YearMonth> rango,
                                             Map<String, Map<YearMonth, Importes>> bancos) {
    Map<YearMonth, Importes> porMes = vacio(rango);
    List<String> claves = claves(rango);
    switch (entidad) {
      case REVOLUT -> revolutBalanceDrivenPort.findByMesIn(claves).forEach(
          balance -> acumular(porMes, mesDe(balance.getMes()), balance.getBalanceMensual(), balance.getAporteMensual()));
      case EQUITO -> equitoBalanceDrivenPort.findByMesIn(claves).forEach(
          balance -> acumular(porMes, mesDe(balance.getMes()), balance.getBalanceMensual(), balance.getAporteMensual()));
      case URBANITAE -> urbanitaeBalanceDrivenPort.findByMesIn(claves).forEach(
          balance -> acumular(porMes, mesDe(balance.getMes()), balance.getBalanceMensual(), balance.getAporteMensual()));
      case MINTOS -> interesAnualMintosDrivenPort.findByMesIn(claves).forEach(
          interes -> acumular(porMes, mesDe(interes.getMes()), interes.getValorFinal(), interes.getImporteAñadido()));
      case B100 -> {
        for (TipoSubcuentaB100 tipoSubcuenta : TipoSubcuentaB100.values()) {
          b100BalanceDrivenPort.findByTipoSubcuentaAndMesIn(tipoSubcuenta, claves).forEach(
              balance -> acumular(porMes, mesDe(balance.getMes()), balance.getBalanceMensual(), balance.getAporteMensual()));
        }
      }
      case MYINVESTOR -> fundBalanceDrivenPort.findByAnioInAndMesIn(anios(rango), numerosDeMes(rango)).stream()
          .filter(fondo -> rango.contains(YearMonth.of(fondo.getAnio(), fondo.getMes())))
          .forEach(fondo -> acumular(porMes, YearMonth.of(fondo.getAnio(), fondo.getMes()),
              fondo.getSaldo(), fondo.getAportacion()));
      case GASTOS -> rango.forEach(mesDelRango -> porMes.put(mesDelRango, new Importes(
          gastoDrivenPort.findByPeriodo(mesDelRango.getYear(), mesDelRango.getMonthValue(), "asc").stream()
              .map(Gasto::getCantidad)
              .reduce(BigDecimal.ZERO, BigDecimal::add),
          BigDecimal.ZERO)));
      case BBVA, CAIXABANK -> {
        Map<YearMonth, Importes> delBanco = bancos.get(entidad.getCodigo());
        if (delBanco != null) {
          porMes.putAll(delBanco);
        }
      }
      case CRYPTO, ACCIONES -> {
      }
    }
    return porMes;
  }

  private Map<String, Map<YearMonth, Importes>> importesDeBancos(List<String> entidades, List<YearMonth> rango) {
    Map<String, Map<YearMonth, Importes>> porBanco = new LinkedHashMap<>();
    entidades.forEach(codigo -> porBanco.put(codigo, vacio(rango)));
    if (entidades.isEmpty()) {
      return porBanco;
    }
    bancoBalanceDrivenPort.findByEntidadInAndMesIn(entidades, claves(rango)).forEach(balance -> {
      Map<YearMonth, Importes> delBanco = porBanco.get(balance.getEntidad());
      if (delBanco != null) {
        acumular(delBanco, mesDe(balance.getMes()), balance.getBalanceMensual(), balance.getAporteMensual());
      }
    });
    return porBanco;
  }

  private Resolucion resolverEntidad(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new InvalidRequestException("La entidad es obligatoria");
    }
    String normalizado = codigo.trim();
    Optional<EntidadFinanciera> entidad = EntidadFinanciera.porCodigo(normalizado);
    if (entidad.isPresent()) {
      return new Resolucion(normalizado, entidad.get().name(), entidad.get().esBanco());
    }
    boolean esBanco = bancoBalanceDrivenPort.findEntidades().stream()
        .anyMatch(conocido -> conocido.equalsIgnoreCase(normalizado));
    if (esBanco) {
      return new Resolucion(normalizado, null, true);
    }
    throw new InvalidRequestException("Entidad desconocida: " + normalizado);
  }

  private List<String> entidadesBancoConocidas() {
    List<String> delEnum = Arrays.stream(EntidadFinanciera.values())
        .filter(EntidadFinanciera::esBanco)
        .map(EntidadFinanciera::getCodigo)
        .toList();
    List<String> deBaseDatos = bancoBalanceDrivenPort.findEntidades();
    return Stream.concat(delEnum.stream(), deBaseDatos.stream()).distinct().toList();
  }

  private static DashboardEntityBalance filaEntidad(String codigo, String nombre, Importes importes) {
    return DashboardEntityBalance.builder()
        .codigo(codigo)
        .nombre(nombre)
        .balance(importes.balance())
        .aporte(importes.aporte())
        .build();
  }

  private static void acumular(Map<YearMonth, Importes> porMes, YearMonth mes, BigDecimal balance, BigDecimal aporte) {
    porMes.merge(mes, new Importes(valor(balance), valor(aporte)),
        (acumulado, nuevo) -> new Importes(
            acumulado.balance().add(nuevo.balance()),
            acumulado.aporte().add(nuevo.aporte())));
  }

  private static BigDecimal valor(BigDecimal importe) {
    return importe != null ? importe : BigDecimal.ZERO;
  }

  private static Map<YearMonth, Importes> vacio(List<YearMonth> rango) {
    Map<YearMonth, Importes> porMes = new LinkedHashMap<>();
    rango.forEach(mes -> porMes.put(mes, Importes.VACIO));
    return porMes;
  }

  private static List<String> claves(List<YearMonth> rango) {
    return rango.stream().map(YearMonth::toString).toList();
  }

  private static List<Integer> anios(List<YearMonth> rango) {
    return rango.stream().map(YearMonth::getYear).distinct().toList();
  }

  private static List<Integer> numerosDeMes(List<YearMonth> rango) {
    return rango.stream().map(YearMonth::getMonthValue).distinct().toList();
  }

  private static YearMonth mesDe(String mes) {
    return YearMonth.parse(mes);
  }

  private static List<YearMonth> mesesDelRango(YearMonth ancla, Integer mesesAtras) {
    int meses = mesesAtras != null ? mesesAtras : MESES_ATRAS_POR_DEFECTO;
    if (meses < 0 || meses > MESES_ATRAS_MAXIMO) {
      throw new InvalidRequestException("mesesAtras debe estar entre 0 y " + MESES_ATRAS_MAXIMO);
    }
    return IntStream.rangeClosed(0, meses)
        .mapToObj(desplazamiento -> ancla.minusMonths(meses - desplazamiento))
        .toList();
  }

  private static YearMonth parsearMes(String mes) {
    if (mes == null || mes.isBlank()) {
      throw new InvalidRequestException("El mes es obligatorio");
    }
    try {
      return YearMonth.parse(mes.trim());
    } catch (DateTimeParseException ex) {
      throw new InvalidRequestException("Mes inválido (se espera YYYY-MM): " + mes);
    }
  }

  private record Resolucion(String codigo, String entidad, boolean esBanco) {
  }

  private record Importes(BigDecimal balance, BigDecimal aporte) {

    private static final Importes VACIO = new Importes(BigDecimal.ZERO, BigDecimal.ZERO);

    private boolean tieneDatos() {
      return balance.signum() != 0 || aporte.signum() != 0;
    }
  }
}
