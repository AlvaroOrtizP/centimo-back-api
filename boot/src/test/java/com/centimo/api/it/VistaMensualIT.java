package com.centimo.api.it;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/it/vistamensual/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Vista Mensual — contratos de datos del desglose de un mes (migración backend)")
class VistaMensualIT extends AbstractIntegrationIT {

  private static final String DASHBOARD = "/api/v1/dashboard";
  private static final String EXPENSES = "/api/v1/expenses";
  private static final String NOMINA = "/api/v1/nomina";
  private static final String B100 = "/api/v1/b100-balances";
  private static final String FUND_BALANCES = "/api/v1/fund-balances";
  private static final String REVOLUT = "/api/v1/revolut-balances";

  @Test
  @DisplayName("balance total y desglose por entidad desde /dashboard/balances")
  void balanceTotalYDesglosePorEntidad() throws Exception {
    seedMes();

    mockMvc.perform(get(DASHBOARD + "/balances").param("mes", "2026-06"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.mes").value("2026-06"))
        .andExpect(jsonPath("$.total").value(2150.0))
        .andExpect(jsonPath("$.entidades.length()").value(6))
        .andExpect(jsonPath("$.entidades[0].codigo").value("revolut"))
        .andExpect(jsonPath("$.entidades[0].balance").value(100.0))
        .andExpect(jsonPath("$.entidades[1].codigo").value("mintos"))
        .andExpect(jsonPath("$.entidades[1].balance").value(600.0))
        .andExpect(jsonPath("$.entidades[2].codigo").value("b100"))
        .andExpect(jsonPath("$.entidades[2].balance").value(500.0))
        .andExpect(jsonPath("$.entidades[2].aporte").value(0.0))
        .andExpect(jsonPath("$.entidades[3].codigo").value("myinvestor"))
        .andExpect(jsonPath("$.entidades[3].balance").value(500.0))
        .andExpect(jsonPath("$.entidades[4].codigo").value("bbva"))
        .andExpect(jsonPath("$.entidades[4].balance").value(400.0))
        .andExpect(jsonPath("$.entidades[5].codigo").value("caixabank"))
        .andExpect(jsonPath("$.entidades[5].balance").value(50.0));
  }

  @Test
  @DisplayName("total de gastos del mes desde /dashboard/balances/serie?entidad=gastos")
  void gastosDelMes() throws Exception {
    seedMes();

    mockMvc.perform(get(DASHBOARD + "/balances/serie")
            .param("mes", "2026-06")
            .param("entidad", "gastos")
            .param("mesesAtras", "0"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].mes").value("2026-06"))
        .andExpect(jsonPath("$[0].balance").value(161.23))
        .andExpect(jsonPath("$[0].aporte").value(0.0));
  }

  @Test
  @DisplayName("gastos desglosados por categoría desde /expenses?year=&month=")
  void gastosPorCategoria() throws Exception {
    seedMes();

    mockMvc.perform(get(EXPENSES)
            .param("year", "2026")
            .param("month", "6"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(4))
        .andExpect(jsonPath("$..category").value(containsInAnyOrder("Comida", "Comida", "Ocio", "Trabajo")));
  }

  @Test
  @DisplayName("ingresos del mes desde /nomina?mes=")
  void ingresosDelMes() throws Exception {
    seedMes();

    mockMvc.perform(get(NOMINA).param("mes", "2026-06"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.mes").value("2026-06"))
        .andExpect(jsonPath("$.cantidad").value(1500.0));
  }

  @Test
  @DisplayName("desglose por cuenta/subcuenta desde los endpoints propios de cada entidad")
  void desglosePorCuentaYSubcuenta() throws Exception {
    seedMes();

    mockMvc.perform(get(B100).param("tipoSubcuenta", "save").param("mes", "2026-06"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].balanceMensual").value(200.0));

    mockMvc.perform(get(B100).param("tipoSubcuenta", "health").param("mes", "2026-06"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].balanceMensual").value(300.0));

    mockMvc.perform(get(FUND_BALANCES).param("year", "2026").param("month", "6"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].balance").value(500.0));

    mockMvc.perform(get(REVOLUT))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].balanceMensual").value(100.0));
  }

  private void seedMes() throws Exception {
    revolut("2026-06", 100.00, 0.00);
    b100("save", "2026-06", 200.00, 0.00);
    b100("health", "2026-06", 300.00, 0.00);
    banco("bbva", "2026-06", 400.00, 0.00);
    banco("caixabank", "2026-06", 50.00, 0.00);
    mintos("2026-06", 600.00, 0.00);
    fondo("f1", "Fondo 1");
    balanceFondo("f1", 2026, 6, 500.00, 0.00);
    nomina("2026-06", 1500.00);
    gasto("2026-06-03", 20.00, "Comida");
    gasto("2026-06-10", 35.00, "Comida");
    gasto("2026-06-14", 6.23, "Ocio");
    gasto("2026-06-20", 100.00, "Trabajo");
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
    mockMvc.perform(post("/api/v1/banco/balances")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"entidad":"%s","mes":"%s","balanceMensual":%s,"aporteMensual":%s}
                """.formatted(entidad, mes, balance, aporte)))
        .andExpect(status().isCreated());
  }

  private void mintos(String mes, double valorFinal, double importeAñadido) throws Exception {
    mockMvc.perform(post("/api/v1/mintos/intereses-anuales")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"mes":"%s","valorFinal":%s,"importeAñadido":%s}
                """.formatted(mes, valorFinal, importeAñadido)))
        .andExpect(status().isCreated());
  }

  private void fondo(String id, String nombre) throws Exception {
    mockMvc.perform(post("/api/v1/myinvestor-funds")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id":"%s","name":"%s","tipo":"fondo"}
                """.formatted(id, nombre)))
        .andExpect(status().isCreated());
  }

  private void balanceFondo(String fundId, int year, int month, double balance, double contribution) throws Exception {
    mockMvc.perform(post(FUND_BALANCES)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"fundId":"%s","year":%s,"month":%s,"balance":%s,"contribution":%s}
                """.formatted(fundId, year, month, balance, contribution)))
        .andExpect(status().isCreated());
  }

  private void nomina(String mes, double cantidad) throws Exception {
    mockMvc.perform(post(NOMINA)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"mes":"%s","cantidad":%s}
                """.formatted(mes, cantidad)))
        .andExpect(status().isCreated());
  }

  private void gasto(String fecha, double cantidad, String categoria) throws Exception {
    mockMvc.perform(post(EXPENSES)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"category":"%s","amount":%s,"date":"%s"}
                """.formatted(categoria, cantidad, fecha)))
        .andExpect(status().isCreated());
  }
}