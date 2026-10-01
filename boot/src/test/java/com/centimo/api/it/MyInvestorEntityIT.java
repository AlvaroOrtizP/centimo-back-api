package com.centimo.api.it;

import com.centimo.api.it.support.StatisticsAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/it/myinvestor/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("MyInvestor — CRUD de activos (fondos y roboadvisor) y balances mensuales")
class MyInvestorEntityIT extends AbstractIntegrationIT {

  private static final String FUND_BASE = "/api/v1/myinvestor-funds";
  private static final String BALANCE_BASE = "/api/v1/fund-balances";
  private static final String ENTIDAD_FONDO = "com.centimo.api.database.models.FondoMyInvestorMO";
  private static final String ENTIDAD_BALANCE = "com.centimo.api.database.models.BalanceFondoMO";

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Nested
  @DisplayName("POST /myinvestor-funds")
  class CrearFondo {

    @Test
    @DisplayName("crea un fondo con id, ISIN, nombre y tipo")
    void creaFondo() throws Exception {
      mockMvc.perform(post(FUND_BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "id": "mi-msci-word",
                    "code": "IE00BYX2JR69",
                    "name": "MSCI World",
                    "tipo": "fondo"
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("mi-msci-word"))
          .andExpect(jsonPath("$.code").value("IE00BYX2JR69"))
          .andExpect(jsonPath("$.name").value("MSCI World"))
          .andExpect(jsonPath("$.tipo").value("fondo"));

      var row = jdbcTemplate.queryForMap(
          "SELECT codigo_isin, nombre, tipo FROM fondos_myinvestor WHERE id = ?",
          "mi-msci-word");
      assertThat(row.get("codigo_isin")).isEqualTo("IE00BYX2JR69");
      assertThat(row.get("nombre")).isEqualTo("MSCI World");
      assertThat(row.get("tipo")).isEqualTo("fondo");

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD_FONDO)
          .hasInsertCount(1)
          .verify();
    }

    @Test
    @DisplayName("crea un roboadvisor sin ISIN (code nulo)")
    void creaRoboadvisor() throws Exception {
      mockMvc.perform(post(FUND_BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "id": "mi-robo",
                    "name": "Roboadvisor Indexado",
                    "tipo": "roboadvisor"
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("mi-robo"))
          .andExpect(jsonPath("$.code").isEmpty())
          .andExpect(jsonPath("$.tipo").value("roboadvisor"));

      assertThat(jdbcTemplate.queryForObject(
          "SELECT codigo_isin FROM fondos_myinvestor WHERE id = ?", Object.class, "mi-robo")).isNull();
    }
  }

  @Nested
  @DisplayName("GET /myinvestor-funds")
  class ListarFondos {

    @Test
    @DisplayName("devuelve los dos fondos y el roboadvisor")
    void listaActivos() throws Exception {
      crearFondo("mi-msci-word", "IE00BYX2JR69", "MSCI World", "fondo");
      crearFondo("mi-security", "IE00B3XXRP09", "Global Security", "fondo");
      crearFondo("mi-robo", null, "Roboadvisor Indexado", "roboadvisor");

      mockMvc.perform(get(FUND_BASE))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(3))
          .andExpect(jsonPath("$[?(@.id=='mi-robo')].tipo").value("roboadvisor"));
    }

    @Test
    @DisplayName("obtiene un activo por su id")
    void obtenerPorId() throws Exception {
      crearFondo("mi-msci-word", "IE00BYX2JR69", "MSCI World", "fondo");

      mockMvc.perform(get(FUND_BASE + "/mi-msci-word"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("mi-msci-word"))
          .andExpect(jsonPath("$.name").value("MSCI World"));
    }
  }

  @Nested
  @DisplayName("PUT /myinvestor-funds/{id}")
  class ActualizarFondo {

    @Test
    @DisplayName("actualiza nombre e ISIN")
    void actualizaFondo() throws Exception {
      crearFondo("mi-msci-word", "IE00BYX2JR69", "MSCI World", "fondo");

      mockMvc.perform(put(FUND_BASE + "/mi-msci-word")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "name": "MSCI World (EUR)"
                  }
                  """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("mi-msci-word"))
          .andExpect(jsonPath("$.name").value("MSCI World (EUR)"))
          .andExpect(jsonPath("$.code").value("IE00BYX2JR69"))
          .andExpect(jsonPath("$.tipo").value("fondo"));

      assertThat(jdbcTemplate.queryForObject(
          "SELECT nombre FROM fondos_myinvestor WHERE id = ?", String.class, "mi-msci-word"))
          .isEqualTo("MSCI World (EUR)");
    }
  }

  @Nested
  @DisplayName("DELETE /myinvestor-funds/{id}")
  class EliminarFondo {

    @Test
    @DisplayName("elimina el activo y borra en cascada sus balances")
    void eliminaConCascada() throws Exception {
      crearFondo("mi-msci-word", "IE00BYX2JR69", "MSCI World", "fondo");
      crearBalance("mi-msci-word", 2026, 1, 1000.00);
      assertThat(countFondos()).isEqualTo(1);
      assertThat(countBalances()).isEqualTo(1);

      mockMvc.perform(delete(FUND_BASE + "/mi-msci-word"))
          .andExpect(status().isNoContent());

      assertThat(countFondos()).isZero();
    }
  }

  @Nested
  @DisplayName("POST /fund-balances")
  class CrearBalance {

    @Test
    @DisplayName("crea el balance de un mes para un activo con defaults en los opcionales")
    void creaConDefaults() throws Exception {
      crearFondo("mi-msci-word", "IE00BYX2JR69", "MSCI World", "fondo");

      mockMvc.perform(post(BALANCE_BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "fundId": "mi-msci-word",
                    "year": 2026,
                    "month": 1,
                    "balance": 1250.50
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.fundId").value("mi-msci-word"))
          .andExpect(jsonPath("$.year").value(2026))
          .andExpect(jsonPath("$.month").value(1))
          .andExpect(jsonPath("$.balance").value(1250.5))
          .andExpect(jsonPath("$.income").value(0.0))
          .andExpect(jsonPath("$.contribution").value(0.0))
          .andExpect(jsonPath("$.expenses").value(0.0));

      var row = jdbcTemplate.queryForMap(
          "SELECT fondo_id, anio, mes, saldo, intereses, aportacion, retirada "
              + "FROM balances_fondo WHERE fondo_id = ?",
          "mi-msci-word");
      assertThat(row.get("anio")).isEqualTo(2026);
      assertThat(row.get("mes")).isEqualTo(1);
      assertThat((BigDecimal) row.get("saldo")).isEqualByComparingTo("1250.50");
      assertThat((BigDecimal) row.get("intereses")).isEqualByComparingTo("0.00");
      assertThat((BigDecimal) row.get("aportacion")).isEqualByComparingTo("0.00");
      assertThat((BigDecimal) row.get("retirada")).isEqualByComparingTo("0.00");

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD_BALANCE)
          .hasInsertCount(1)
          .and(ENTIDAD_FONDO)
          .hasInsertCount(1)
          .hasLoadCount(1)
          .verify();
    }

    @Test
    @DisplayName("respeta intereses, aporte y retirada enviados")
    void respetaOpcionales() throws Exception {
      crearFondo("mi-security", "IE00B3XXRP09", "Global Security", "fondo");

      mockMvc.perform(post(BALANCE_BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "fundId": "mi-security",
                    "year": 2026,
                    "month": 2,
                    "balance": 2000.00,
                    "income": 12.30,
                    "contribution": 100.00,
                    "expenses": 0.00
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.income").value(12.3))
          .andExpect(jsonPath("$.contribution").value(100.0))
          .andExpect(jsonPath("$.expenses").value(0.0));
    }

    @Test
    @DisplayName("repetir fondo, año y mes actualiza sin duplicar")
    void upsertNoDuplica() throws Exception {
      crearFondo("mi-msci-word", "IE00BYX2JR69", "MSCI World", "fondo");
      crearBalance("mi-msci-word", 2026, 3, 1000.00);

      mockMvc.perform(post(BALANCE_BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "fundId": "mi-msci-word",
                    "year": 2026,
                    "month": 3,
                    "balance": 1234.56
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.balance").value(1234.56));

      assertThat(countBalances()).isEqualTo(1);
      assertThat(jdbcTemplate.queryForObject(
          "SELECT saldo FROM balances_fondo WHERE fondo_id = ?", BigDecimal.class, "mi-msci-word"))
          .isEqualByComparingTo("1234.56");
    }
  }

  @Nested
  @DisplayName("GET /fund-balances")
  class ListarBalances {

    @Test
    @DisplayName("lista los balances de todos los activos para un mes concreto")
    void listaPorMes() throws Exception {
      crearFondo("mi-msci-word", "IE00BYX2JR69", "MSCI World", "fondo");
      crearFondo("mi-security", "IE00B3XXRP09", "Global Security", "fondo");
      crearBalance("mi-msci-word", 2026, 1, 1000.00);
      crearBalance("mi-security", 2026, 1, 2000.00);
      crearBalance("mi-msci-word", 2026, 2, 1100.00);

      mockMvc.perform(get(BALANCE_BASE).param("year", "2026").param("month", "1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2));

      mockMvc.perform(get(BALANCE_BASE).param("year", "2026").param("month", "2"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0].fundId").value("mi-msci-word"));
    }
  }

  @Nested
  @DisplayName("PUT /fund-balances/{id}")
  class ActualizarBalance {

    @Test
    @DisplayName("actualiza balance, intereses, aporte y retirada")
    void actualizaBalance() throws Exception {
      crearFondo("mi-msci-word", "IE00BYX2JR69", "MSCI World", "fondo");
      crearBalance("mi-msci-word", 2026, 4, 1000.00, 10.00, 100.00, 0.00);

      String id = jdbcTemplate.queryForObject(
          "SELECT id FROM balances_fondo WHERE fondo_id = 'mi-msci-word'", String.class);

      mockMvc.perform(put(BALANCE_BASE + "/" + id)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "balance": 1500.00,
                    "income": 15.00
                  }
                  """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.balance").value(1500.0))
          .andExpect(jsonPath("$.income").value(15.0))
          .andExpect(jsonPath("$.contribution").value(100.0))
          .andExpect(jsonPath("$.expenses").value(0.0));

      assertThat(countBalances()).isEqualTo(1);
    }
  }

  @Nested
  @DisplayName("DELETE /fund-balances/{id}")
  class EliminarBalance {

    @Test
    @DisplayName("elimina el balance indicado sin borrar el activo")
    void elimina() throws Exception {
      crearFondo("mi-msci-word", "IE00BYX2JR69", "MSCI World", "fondo");
      crearBalance("mi-msci-word", 2026, 6, 1000.00);
      assertThat(countBalances()).isEqualTo(1);

      String id = jdbcTemplate.queryForObject(
          "SELECT id FROM balances_fondo WHERE fondo_id = 'mi-msci-word'", String.class);

      mockMvc.perform(delete(BALANCE_BASE + "/" + id))
          .andExpect(status().isNoContent());

      assertThat(countBalances()).isZero();
      assertThat(countFondos()).isEqualTo(1);

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD_BALANCE)
          .hasInsertCount(1)
          .hasDeleteCount(1)
          .hasLoadCount(1)
          .and(ENTIDAD_FONDO)
          .hasInsertCount(1)
          .hasLoadCount(1)
          .verify();
    }
  }

  private void crearFondo(String id, String code, String name, String tipo) throws Exception {
    String codeJson = code != null ? "\"code\":\"%s\",".formatted(code) : "";
    mockMvc.perform(post(FUND_BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id":"%s",%s"name":"%s","tipo":"%s"}
                """.formatted(id, codeJson, name, tipo)))
        .andExpect(status().isCreated());
  }

  private void crearBalance(String fundId, int year, int month, double balance) throws Exception {
    mockMvc.perform(post(BALANCE_BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"fundId":"%s","year":%s,"month":%s,"balance":%s}
                """.formatted(fundId, year, month, balance)))
        .andExpect(status().isCreated());
  }

  private void crearBalance(String fundId, int year, int month, double balance,
                            double income, double contribution, double expenses) throws Exception {
    mockMvc.perform(post(BALANCE_BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"fundId":"%s","year":%s,"month":%s,"balance":%s,"income":%s,"contribution":%s,"expenses":%s}
                """.formatted(fundId, year, month, balance, income, contribution, expenses)))
        .andExpect(status().isCreated());
  }

  private int countFondos() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM fondos_myinvestor", Integer.class);
  }

  private int countBalances() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM balances_fondo", Integer.class);
  }
}