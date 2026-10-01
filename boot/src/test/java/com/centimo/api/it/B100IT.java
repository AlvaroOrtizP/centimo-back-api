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

@Sql(scripts = "/it/b100/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("B100 — CRUD de balances por subcuenta")
class B100IT extends AbstractIntegrationIT {

  private static final String BASE = "/api/v1/b100-balances";
  private static final String ENTIDAD = "com.centimo.api.database.models.B100BalanceMO";

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Nested
  @DisplayName("POST /b100-balances (upsert por subcuenta y mes)")
  class Crear {

    @Test
    @DisplayName("crea la fila con id natural y aplica defaults en campos opcionales")
    void creaConIdNaturalYDefaults() throws Exception {
      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "tipoSubcuenta": "save",
                    "mes": "2026-01",
                    "balanceMensual": 100.50,
                    "dineroTotalRepartir": 200.00
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("save-2026-01"))
          .andExpect(jsonPath("$.tipoSubcuenta").value("save"))
          .andExpect(jsonPath("$.mes").value("2026-01"))
          .andExpect(jsonPath("$.balanceMensual").value(100.5))
          .andExpect(jsonPath("$.aporteMensual").value(0.0))
          .andExpect(jsonPath("$.dineroHacienda").value(0.0))
          .andExpect(jsonPath("$.porcentajeHacienda").value(19.0))
          .andExpect(jsonPath("$.dineroTotalRepartir").value(200.0));

      var row = jdbcTemplate.queryForMap(
          "SELECT tipo_subcuenta, mes, balance_mensual, aporte_mensual, dinero_hacienda, "
              + "dinero_total_repartir, porcentaje_hacienda FROM b100_balances WHERE id = ?",
          "save-2026-01");
      assertThat(row.get("tipo_subcuenta")).isEqualTo("save");
      assertThat(row.get("mes")).isEqualTo("2026-01");
      assertThat((BigDecimal) row.get("balance_mensual")).isEqualByComparingTo("100.50");
      assertThat((BigDecimal) row.get("aporte_mensual")).isEqualByComparingTo("0.00");
      assertThat((BigDecimal) row.get("dinero_hacienda")).isEqualByComparingTo("0.00");
      assertThat((BigDecimal) row.get("dinero_total_repartir")).isEqualByComparingTo("200.00");
      assertThat((BigDecimal) row.get("porcentaje_hacienda")).isEqualByComparingTo("19.00");
      assertThat(jdbcTemplate.queryForObject(
          "SELECT fecha_creacion IS NOT NULL FROM b100_balances WHERE id = ?", Boolean.class, "save-2026-01"))
          .isTrue();

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD)
          .hasInsertCount(1)
          .verify();
    }

    @Test
    @DisplayName("respeta los opcionales enviados")
    void respetaOpcionales() throws Exception {
      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "tipoSubcuenta": "health",
                    "mes": "2026-02",
                    "balanceMensual": 50.00,
                    "aporteMensual": 25.00,
                    "dineroHacienda": 4.75,
                    "porcentajeHacienda": 19.00,
                    "dineroTotalRepartir": 150.00
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("health-2026-02"))
          .andExpect(jsonPath("$.aporteMensual").value(25.0))
          .andExpect(jsonPath("$.dineroHacienda").value(4.75))
          .andExpect(jsonPath("$.porcentajeHacienda").value(19.0));
    }

    @Test
    @DisplayName("repetir (subcuenta, mes) actualiza la fila sin duplicar y conserva fecha_creacion")
    void upsertNoDuplica() throws Exception {
      crear("save", "2026-03", 10.00, 100.00);

      var fechaCreacionOriginal = jdbcTemplate.queryForObject(
          "SELECT fecha_creacion FROM b100_balances WHERE id = ?", Object.class, "save-2026-03");

      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "tipoSubcuenta": "save",
                    "mes": "2026-03",
                    "balanceMensual": 99.99,
                    "dineroTotalRepartir": 300.00
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("save-2026-03"))
          .andExpect(jsonPath("$.balanceMensual").value(99.99));

      assertThat(countRows()).isEqualTo(1);
      assertThat(jdbcTemplate.queryForObject(
          "SELECT balance_mensual FROM b100_balances WHERE id = ?", BigDecimal.class, "save-2026-03"))
          .isEqualByComparingTo("99.99");
      assertThat(jdbcTemplate.queryForObject(
          "SELECT fecha_creacion FROM b100_balances WHERE id = ?", Object.class, "save-2026-03"))
          .isEqualTo(fechaCreacionOriginal);

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD)
          .hasInsertCount(1)
          .hasUpdateCount(1)
          .hasLoadCount(1)
          .verify();
    }
  }

  @Nested
  @DisplayName("GET /b100-balances")
  class Listar {

    @Test
    @DisplayName("por defecto devuelve los meses ≤ mes de la subcuenta pedida, ordenados desc")
    void listaDescendentePorDefecto() throws Exception {
      crear("save", "2026-01", 1.00, 10.00);
      crear("save", "2026-02", 2.00, 20.00);
      crear("save", "2026-03", 3.00, 30.00);
      crear("save", "2026-04", 4.00, 40.00);
      crear("health", "2026-05", 5.00, 50.00);

      mockMvc.perform(get(BASE).param("tipoSubcuenta", "save").param("mes", "2026-03"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(3))
          .andExpect(jsonPath("$[0].mes").value("2026-03"))
          .andExpect(jsonPath("$[1].mes").value("2026-02"))
          .andExpect(jsonPath("$[2].mes").value("2026-01"));
    }

    @Test
    @DisplayName("order=asc devuelve los meses ≥ mes y limit recorta el resultado")
    void listaAscendenteConLimite() throws Exception {
      crear("save", "2026-01", 1.00, 10.00);
      crear("save", "2026-02", 2.00, 20.00);
      crear("save", "2026-03", 3.00, 30.00);

      mockMvc.perform(get(BASE).param("tipoSubcuenta", "save").param("mes", "2026-01")
              .param("order", "asc").param("limit", "2"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[0].mes").value("2026-01"))
          .andExpect(jsonPath("$[1].mes").value("2026-02"));
    }

    @Test
    @DisplayName("el mes de partida filtra hacia delante con order=asc (excluye los anteriores)")
    void mesFiltraHaciaAdelante() throws Exception {
      crear("save", "2026-01", 1.00, 10.00);
      crear("save", "2026-02", 2.00, 20.00);
      crear("save", "2026-03", 3.00, 30.00);

      mockMvc.perform(get(BASE).param("tipoSubcuenta", "save").param("mes", "2026-02")
              .param("order", "asc"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[0].mes").value("2026-02"))
          .andExpect(jsonPath("$[1].mes").value("2026-03"));
    }

    @Test
    @DisplayName("sin datos devuelve lista vacía")
    void listaVacia() throws Exception {
      mockMvc.perform(get(BASE).param("tipoSubcuenta", "save").param("mes", "2026-01"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("tipo de subcuenta inválido devuelve 400")
    void tipoInvalido() throws Exception {
      mockMvc.perform(get(BASE).param("tipoSubcuenta", "inexistente").param("mes", "2026-01"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("PUT /b100-balances/{id}")
  class Actualizar {

    @Test
    @DisplayName("actualiza los campos enviados y conserva los opcionales omitidos")
    void actualizaCamposEnviados() throws Exception {
      crear("health", "2026-04", 100.00, 500.00, 40.00, 8.00, 19.00);

      mockMvc.perform(put(BASE + "/health-2026-04")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "tipoSubcuenta": "health",
                    "mes": "2026-04",
                    "balanceMensual": 123.45,
                    "dineroTotalRepartir": 900.00
                  }
                  """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("health-2026-04"))
          .andExpect(jsonPath("$.balanceMensual").value(123.45))
          .andExpect(jsonPath("$.dineroTotalRepartir").value(900.0))
          .andExpect(jsonPath("$.aporteMensual").value(40.0))
          .andExpect(jsonPath("$.dineroHacienda").value(8.0))
          .andExpect(jsonPath("$.porcentajeHacienda").value(19.0));

      assertThat(countRows()).isEqualTo(1);
    }
  }

  @Nested
  @DisplayName("DELETE /b100-balances/{id}")
  class Eliminar {

    @Test
    @DisplayName("elimina la fila indicada")
    void elimina() throws Exception {
      crear("save", "2026-06", 10.00, 100.00);
      assertThat(countRows()).isEqualTo(1);

      mockMvc.perform(delete(BASE + "/save-2026-06"))
          .andExpect(status().isNoContent());

      assertThat(countRows()).isZero();

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD)
          .hasInsertCount(1)
          .hasDeleteCount(1)
          .hasLoadCount(1)
          .verify();
    }
  }

  private void crear(String tipoSubcuenta, String mes, double balance, double totalRepartir) throws Exception {
    mockMvc.perform(post(BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"tipoSubcuenta":"%s","mes":"%s","balanceMensual":%s,"dineroTotalRepartir":%s}
                """.formatted(tipoSubcuenta, mes, balance, totalRepartir)))
        .andExpect(status().isCreated());
  }

  private void crear(String tipoSubcuenta, String mes, double balance, double totalRepartir,
                     double aporte, double hacienda, double porcentaje) throws Exception {
    mockMvc.perform(post(BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"tipoSubcuenta":"%s","mes":"%s","balanceMensual":%s,"dineroTotalRepartir":%s,
                 "aporteMensual":%s,"dineroHacienda":%s,"porcentajeHacienda":%s}
                """.formatted(tipoSubcuenta, mes, balance, totalRepartir, aporte, hacienda, porcentaje)))
        .andExpect(status().isCreated());
  }

  private int countRows() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM b100_balances", Integer.class);
  }
}
