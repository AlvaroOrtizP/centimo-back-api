package com.centimo.api.it;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/it/dashboard/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Dashboard — balance del mes, serie por entidad y serie por categoría")
class DashboardIT extends AbstractIntegrationIT {

  private static final String BASE = "/api/v1/dashboard";
  private static final String REVOLUT = "/api/v1/revolut-balances";
  private static final String B100 = "/api/v1/b100-balances";
  private static final String BANCO = "/api/v1/banco/balances";
  private static final String MINTOS = "/api/v1/mintos/intereses-anuales";
  private static final String EQUITO = "/api/v1/equito/balances";
  private static final String URBANITAE = "/api/v1/urbanitae/balances";
  private static final String EXPENSES = "/api/v1/expenses";
  private static final String FUNDS = "/api/v1/myinvestor-funds";
  private static final String FUND_BALANCES = "/api/v1/fund-balances";

  @Nested
  @DisplayName("GET /dashboard/balances")
  class BalanceDelMes {

    @Test
    @DisplayName("devuelve una fila por entidad con su agregado y el total, sin gastos")
    void totalYDetallePorEntidad() throws Exception {
      revolut("2026-06", 100.00, 0.00);
      b100("save", "2026-06", 200.00, 50.00);
      b100("health", "2026-06", 300.00, 25.00);
      banco("bbva", "2026-06", 400.00, 10.00);
      banco("caixabank", "2026-06", 50.00, 0.00);
      mintos("2026-06", 600.00, 0.00);
      gasto("2026-06-15", 1200.00);

      mockMvc.perform(get(BASE + "/balances").param("mes", "2026-06"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.mes").value("2026-06"))
          .andExpect(jsonPath("$.total").value(1650.0))
          .andExpect(jsonPath("$.entidades.length()").value(5))
          .andExpect(jsonPath("$.entidades[0].codigo").value("revolut"))
          .andExpect(jsonPath("$.entidades[0].nombre").value("Revolut"))
          .andExpect(jsonPath("$.entidades[1].codigo").value("mintos"))
          .andExpect(jsonPath("$.entidades[2].codigo").value("b100"))
          .andExpect(jsonPath("$.entidades[2].balance").value(500.0))
          .andExpect(jsonPath("$.entidades[2].aporte").value(75.0))
          .andExpect(jsonPath("$.entidades[3].codigo").value("bbva"))
          .andExpect(jsonPath("$.entidades[4].codigo").value("caixabank"))
          .andExpect(jsonPath("$.entidades[4].balance").value(50.0));
    }

    @Test
    @DisplayName("las entidades sin registro para el mes no aparecen")
    void sinRegistroNoAparece() throws Exception {
      revolut("2026-06", 100.00, 0.00);

      mockMvc.perform(get(BASE + "/balances").param("mes", "2026-06"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.total").value(100.0))
          .andExpect(jsonPath("$.entidades.length()").value(1))
          .andExpect(jsonPath("$.entidades[0].codigo").value("revolut"));

      mockMvc.perform(get(BASE + "/balances").param("mes", "2026-05"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.total").value(0.0))
          .andExpect(jsonPath("$.entidades.length()").value(0));
    }

    @Test
    @DisplayName("un banco en la BD pero no en el catálogo aparece como fila propia")
    void bancoDesconocidoAparece() throws Exception {
      banco("bankinter", "2026-06", 700.00, 0.00);
      revolut("2026-06", 300.00, 0.00);

      mockMvc.perform(get(BASE + "/balances").param("mes", "2026-06"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.total").value(1000.0))
          .andExpect(jsonPath("$.entidades.length()").value(2))
          .andExpect(jsonPath("$.entidades[0].codigo").value("revolut"))
          .andExpect(jsonPath("$.entidades[1].codigo").value("bankinter"));
    }

    @Test
    @DisplayName("mes inválido devuelve 400")
    void mesInvalidoDevuelve400() throws Exception {
      mockMvc.perform(get(BASE + "/balances").param("mes", "2026-13"))
          .andExpect(status().isBadRequest());
      mockMvc.perform(get(BASE + "/balances"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("GET /dashboard/balances/serie")
  class SeriePorEntidad {

    @Test
    @DisplayName("devuelve mesesAtras + 1 filas ascendentes y ceros en los meses sin datos")
    void serieAscendenteConCeros() throws Exception {
      revolut("2026-01", 100.00, 10.00);
      revolut("2026-03", 300.00, 0.00);

      mockMvc.perform(get(BASE + "/balances/serie")
              .param("mes", "2026-03")
              .param("entidad", "revolut")
              .param("mesesAtras", "2"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(3))
          .andExpect(jsonPath("$[0].mes").value("2026-01"))
          .andExpect(jsonPath("$[0].balance").value(100.0))
          .andExpect(jsonPath("$[0].aporte").value(10.0))
          .andExpect(jsonPath("$[1].mes").value("2026-02"))
          .andExpect(jsonPath("$[1].balance").value(0.0))
          .andExpect(jsonPath("$[1].aporte").value(0.0))
          .andExpect(jsonPath("$[2].mes").value("2026-03"))
          .andExpect(jsonPath("$[2].balance").value(300.0));
    }

    @Test
    @DisplayName("el rango cruza de año con normalidad")
    void rangoCruzaAnio() throws Exception {
      revolut("2025-12", 10.00, 0.00);
      revolut("2026-01", 20.00, 0.00);

      mockMvc.perform(get(BASE + "/balances/serie")
              .param("mes", "2026-01")
              .param("entidad", "revolut")
              .param("mesesAtras", "2"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(3))
          .andExpect(jsonPath("$[0].mes").value("2025-11"))
          .andExpect(jsonPath("$[0].balance").value(0.0))
          .andExpect(jsonPath("$[1].mes").value("2025-12"))
          .andExpect(jsonPath("$[1].balance").value(10.0))
          .andExpect(jsonPath("$[2].mes").value("2026-01"))
          .andExpect(jsonPath("$[2].balance").value(20.0));
    }

    @Test
    @DisplayName("b100 consolida sus subcuentas save y health en una única serie")
    void b100ConsolidaSubcuentas() throws Exception {
      b100("save", "2026-04", 100.00, 10.00);
      b100("health", "2026-04", 200.00, 20.00);
      b100("save", "2026-05", 110.00, 10.00);
      b100("save", "2026-06", 120.00, 10.00);
      b100("health", "2026-06", 30.00, 0.00);

      mockMvc.perform(get(BASE + "/balances/serie")
              .param("mes", "2026-06")
              .param("entidad", "b100")
              .param("mesesAtras", "2"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(3))
          .andExpect(jsonPath("$[0].mes").value("2026-04"))
          .andExpect(jsonPath("$[0].balance").value(300.0))
          .andExpect(jsonPath("$[0].aporte").value(30.0))
          .andExpect(jsonPath("$[1].mes").value("2026-05"))
          .andExpect(jsonPath("$[1].balance").value(110.0))
          .andExpect(jsonPath("$[2].mes").value("2026-06"))
          .andExpect(jsonPath("$[2].balance").value(150.0));
    }

    @Test
    @DisplayName("sin entidad devuelve el total agregado del mes, excluyendo gastos")
    void sinEntidadDevuelveTotalExcluyendoGastos() throws Exception {
      revolut("2026-02", 100.00, 0.00);
      revolut("2026-03", 150.00, 0.00);
      banco("bbva", "2026-02", 50.00, 0.00);
      banco("bbva", "2026-03", 60.00, 0.00);
      gasto("2026-02-10", 1000.00);
      gasto("2026-03-10", 2000.00);

      mockMvc.perform(get(BASE + "/balances/serie")
              .param("mes", "2026-03")
              .param("mesesAtras", "1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[0].mes").value("2026-02"))
          .andExpect(jsonPath("$[0].balance").value(150.0))
          .andExpect(jsonPath("$[1].mes").value("2026-03"))
          .andExpect(jsonPath("$[1].balance").value(210.0));
    }

    @Test
    @DisplayName("gastos es seleccionable como entidad propia por mes")
    void gastosComoEntidadPropia() throws Exception {
      gasto("2026-05-01", 100.00);
      gasto("2026-05-10", 50.50);
      gasto("2026-06-20", 30.00);

      mockMvc.perform(get(BASE + "/balances/serie")
              .param("mes", "2026-06")
              .param("entidad", "gastos")
              .param("mesesAtras", "1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[0].mes").value("2026-05"))
          .andExpect(jsonPath("$[0].balance").value(150.5))
          .andExpect(jsonPath("$[0].aporte").value(0.0))
          .andExpect(jsonPath("$[1].mes").value("2026-06"))
          .andExpect(jsonPath("$[1].balance").value(30.0));
    }

    @Test
    @DisplayName("myinvestor suma el saldo de todos sus activos y trate aportación nula como 0")
    void myinvestorConsolidaActivos() throws Exception {
      fondo("f1", "Fondo 1");
      fondo("f2", "Fondo 2");
      balanceFondo("f1", 2026, 6, 400.00, 50.00);
      balanceFondo("f2", 2026, 6, 100.00, null);

      mockMvc.perform(get(BASE + "/balances/serie")
              .param("mes", "2026-06")
              .param("entidad", "myinvestor")
              .param("mesesAtras", "0"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0].mes").value("2026-06"))
          .andExpect(jsonPath("$[0].balance").value(500.0))
          .andExpect(jsonPath("$[0].aporte").value(50.0));
    }

    @Test
    @DisplayName("entidad desconocida devuelve 400")
    void entidadDesconocidaDevuelve400() throws Exception {
      mockMvc.perform(get(BASE + "/balances/serie")
              .param("mes", "2026-06")
              .param("entidad", "noexiste"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("mesesAtras fuera de rango devuelve 400")
    void mesesAtrasFueraDeRangoDevuelve400() throws Exception {
      mockMvc.perform(get(BASE + "/balances/serie")
              .param("mes", "2026-06")
              .param("mesesAtras", "99"))
          .andExpect(status().isBadRequest());
      mockMvc.perform(get(BASE + "/balances/serie")
              .param("mes", "2026-06")
              .param("mesesAtras", "-1"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("GET /dashboard/categorias/serie")
  class SeriePorCategoria {

    @Test
    @DisplayName("Liquidez devuelve revolut, bbva, caixabank y b100 por cada mes, en ese orden")
    void liquidezDevuelveSusEntidades() throws Exception {
      revolut("2026-05", 100.00, 0.00);
      revolut("2026-06", 110.00, 0.00);
      banco("bbva", "2026-06", 50.00, 0.00);
      banco("caixabank", "2026-06", 25.00, 0.00);
      b100("save", "2026-06", 80.00, 0.00);
      mintos("2026-06", 9999.00, 0.00);

      mockMvc.perform(get(BASE + "/categorias/serie")
              .param("categoria", "Liquidez")
              .param("mes", "2026-06")
              .param("mesesAtras", "1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(8))
          .andExpect(jsonPath("$[0].mes").value("2026-05"))
          .andExpect(jsonPath("$[0].codigo").value("revolut"))
          .andExpect(jsonPath("$[1].codigo").value("bbva"))
          .andExpect(jsonPath("$[1].balance").value(0.0))
          .andExpect(jsonPath("$[2].codigo").value("caixabank"))
          .andExpect(jsonPath("$[3].codigo").value("b100"))
          .andExpect(jsonPath("$[4].codigo").value("revolut"))
          .andExpect(jsonPath("$[4].balance").value(110.0))
          .andExpect(jsonPath("$[5].codigo").value("bbva"))
          .andExpect(jsonPath("$[5].balance").value(50.0))
          .andExpect(jsonPath("$[6].codigo").value("caixabank"))
          .andExpect(jsonPath("$[7].codigo").value("b100"))
          .andExpect(jsonPath("$[7].balance").value(80.0));
    }

    @Test
    @DisplayName("Todas excluye gastos e incluye crypto y acciones a cero")
    void todasExcluyeGastos() throws Exception {
      revolut("2026-06", 100.00, 0.00);
      gasto("2026-06-01", 9999.00);

      mockMvc.perform(get(BASE + "/categorias/serie")
              .param("categoria", "Todas")
              .param("mes", "2026-06")
              .param("mesesAtras", "0"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(10))
          .andExpect(jsonPath("$[0].codigo").value("revolut"))
          .andExpect(jsonPath("$[0].balance").value(100.0))
          .andExpect(jsonPath("$[3].codigo").value("b100"))
          .andExpect(jsonPath("$[4].codigo").value("mintos"))
          .andExpect(jsonPath("$[4].balance").value(0.0))
          .andExpect(jsonPath("$[7].codigo").value("myinvestor"))
          .andExpect(jsonPath("$[8].codigo").value("crypto"))
          .andExpect(jsonPath("$[8].balance").value(0.0))
          .andExpect(jsonPath("$[9].codigo").value("acciones"))
          .andExpect(jsonPath("$[9].balance").value(0.0))
          .andExpect(jsonPath("$..codigo").value(not(hasItem("gastos"))));
    }

    @Test
    @DisplayName("Fija no incluye bancos y sí mintos, equito y urbanitae")
    void fijaIncluyeFijoExcluyeBancos() throws Exception {
      mintos("2026-06", 600.00, 0.00);
      equito("2026-06", 300.00, 0.00);
      urbanitae("2026-06", 500.00, 0.00);
      banco("bbva", "2026-06", 9999.00, 0.00);

      mockMvc.perform(get(BASE + "/categorias/serie")
              .param("categoria", "Fija")
              .param("mes", "2026-06")
              .param("mesesAtras", "0"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(3))
          .andExpect(jsonPath("$[0].codigo").value("mintos"))
          .andExpect(jsonPath("$[0].balance").value(600.0))
          .andExpect(jsonPath("$[1].codigo").value("equito"))
          .andExpect(jsonPath("$[2].codigo").value("urbanitae"))
          .andExpect(jsonPath("$..codigo").value(not(hasItem("bbva"))));
    }

    @Test
    @DisplayName("categoría fuera del conjunto devuelve 400")
    void categoriaInvalidaDevuelve400() throws Exception {
      mockMvc.perform(get(BASE + "/categorias/serie")
              .param("categoria", "Otra")
              .param("mes", "2026-06"))
          .andExpect(status().isBadRequest());
    }
  }

  private void revolut(String mes, double balance, double aporte) throws Exception {
    mockMvc.perform(post(REVOLUT)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"mes":"%s","balanceMensual":%s,"aporteMensual":%s}
                """.formatted(mes, balance, aporte)))
        .andExpect(status().isCreated());
  }

  private void b100(String tipoSubcuenta, String mes, double balance, double aporte) throws Exception {
    mockMvc.perform(post(B100)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"tipoSubcuenta":"%s","mes":"%s","balanceMensual":%s,"aporteMensual":%s,"dineroTotalRepartir":0}
                """.formatted(tipoSubcuenta, mes, balance, aporte)))
        .andExpect(status().isCreated());
  }

  private void banco(String entidad, String mes, double balance, double aporte) throws Exception {
    mockMvc.perform(post(BANCO)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"entidad":"%s","mes":"%s","balanceMensual":%s,"aporteMensual":%s}
                """.formatted(entidad, mes, balance, aporte)))
        .andExpect(status().isCreated());
  }

  private void mintos(String mes, double valorFinal, double importeAñadido) throws Exception {
    mockMvc.perform(post(MINTOS)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"mes":"%s","valorFinal":%s,"importeAñadido":%s}
                """.formatted(mes, valorFinal, importeAñadido)))
        .andExpect(status().isCreated());
  }

  private void equito(String mes, double balance, double aporte) throws Exception {
    mockMvc.perform(post(EQUITO)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"mes":"%s","balanceMensual":%s,"aporteMensual":%s}
                """.formatted(mes, balance, aporte)))
        .andExpect(status().isCreated());
  }

  private void urbanitae(String mes, double balance, double aporte) throws Exception {
    mockMvc.perform(post(URBANITAE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"mes":"%s","balanceMensual":%s,"aporteMensual":%s}
                """.formatted(mes, balance, aporte)))
        .andExpect(status().isCreated());
  }

  private void gasto(String fecha, double cantidad) throws Exception {
    mockMvc.perform(post(EXPENSES)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"category":"Ocio","amount":%s,"date":"%s"}
                """.formatted(cantidad, fecha)))
        .andExpect(status().isCreated());
  }

  private void fondo(String id, String nombre) throws Exception {
    mockMvc.perform(post(FUNDS)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id":"%s","name":"%s","tipo":"fondo"}
                """.formatted(id, nombre)))
        .andExpect(status().isCreated());
  }

  private void balanceFondo(String fundId, int year, int month, double balance, Double contribution) throws Exception {
    String contributionJson = contribution == null ? "null" : String.valueOf(contribution);
    mockMvc.perform(post(FUND_BALANCES)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"fundId":"%s","year":%s,"month":%s,"balance":%s,"contribution":%s}
                """.formatted(fundId, year, month, balance, contributionJson)))
        .andExpect(status().isCreated());
  }
}