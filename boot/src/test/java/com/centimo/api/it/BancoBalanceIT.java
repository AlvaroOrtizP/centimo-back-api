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

@Sql(scripts = "/it/banco/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Banco — CRUD de balances mensuales por entidad")
class BancoBalanceIT extends AbstractIntegrationIT {

  private static final String BASE = "/api/v1/banco/balances";
  private static final String ENTIDAD = "com.centimo.api.database.models.BancoBalanceMO";

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Nested
  @DisplayName("POST /banco/balances (upsert por entidad y mes)")
  class Crear {

    @Test
    @DisplayName("crea la fila con id natural {entidad}-{AAAA-MM} y aplica default en aporte")
    void creaConIdNaturalYDefaults() throws Exception {
      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "entidad": "bbva",
                    "mes": "2026-01",
                    "balanceMensual": 100.50
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("bbva-2026-01"))
          .andExpect(jsonPath("$.entidad").value("bbva"))
          .andExpect(jsonPath("$.mes").value("2026-01"))
          .andExpect(jsonPath("$.balanceMensual").value(100.5))
          .andExpect(jsonPath("$.aporteMensual").value(0.0));

      var row = jdbcTemplate.queryForMap(
          "SELECT entidad, mes, balance_mensual, aporte_mensual FROM banco_balances WHERE id = ?",
          "bbva-2026-01");
      assertThat(row.get("entidad")).isEqualTo("bbva");
      assertThat(row.get("mes")).isEqualTo("2026-01");
      assertThat((BigDecimal) row.get("balance_mensual")).isEqualByComparingTo("100.50");
      assertThat((BigDecimal) row.get("aporte_mensual")).isEqualByComparingTo("0.00");
      assertThat(jdbcTemplate.queryForObject(
          "SELECT fecha_creacion IS NOT NULL FROM banco_balances WHERE id = ?", Boolean.class, "bbva-2026-01"))
          .isTrue();

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD)
          .hasInsertCount(1)
          .verify();
    }

    @Test
    @DisplayName("respeta el aporte enviado")
    void respetaOpcionales() throws Exception {
      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "entidad": "caixabank",
                    "mes": "2026-02",
                    "balanceMensual": 50.00,
                    "aporteMensual": 25.00
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("caixabank-2026-02"))
          .andExpect(jsonPath("$.aporteMensual").value(25.0));
    }

    @Test
    @DisplayName("repetir (entidad, mes) actualiza la fila sin duplicar y conserva fecha_creacion")
    void upsertNoDuplica() throws Exception {
      crear("bbva", "2026-03", 10.00);

      var fechaCreacionOriginal = jdbcTemplate.queryForObject(
          "SELECT fecha_creacion FROM banco_balances WHERE id = ?", Object.class, "bbva-2026-03");

      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "entidad": "bbva",
                    "mes": "2026-03",
                    "balanceMensual": 99.99
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("bbva-2026-03"))
          .andExpect(jsonPath("$.balanceMensual").value(99.99));

      assertThat(countRows()).isEqualTo(1);
      assertThat(jdbcTemplate.queryForObject(
          "SELECT balance_mensual FROM banco_balances WHERE id = ?", BigDecimal.class, "bbva-2026-03"))
          .isEqualByComparingTo("99.99");
      assertThat(jdbcTemplate.queryForObject(
          "SELECT fecha_creacion FROM banco_balances WHERE id = ?", Object.class, "bbva-2026-03"))
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
  @DisplayName("GET /banco/balances")
  class Listar {

    @Test
    @DisplayName("filtra por entidad y ordena por mes descendente por defecto")
    void filtraPorEntidadDescendente() throws Exception {
      crear("bbva", "2026-01", 1.00);
      crear("bbva", "2026-02", 2.00);
      crear("bbva", "2026-03", 3.00);
      crear("caixabank", "2026-05", 5.00);

      mockMvc.perform(get(BASE).param("entidad", "bbva"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(3))
          .andExpect(jsonPath("$[0].mes").value("2026-03"))
          .andExpect(jsonPath("$[1].mes").value("2026-02"))
          .andExpect(jsonPath("$[2].mes").value("2026-01"));
    }

    @Test
    @DisplayName("order=asc invierte el orden y limit recorta el resultado")
    void listaAscendenteConLimite() throws Exception {
      crear("bbva", "2026-01", 1.00);
      crear("bbva", "2026-02", 2.00);
      crear("bbva", "2026-03", 3.00);

      mockMvc.perform(get(BASE).param("entidad", "bbva").param("order", "asc").param("limit", "2"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[0].mes").value("2026-01"))
          .andExpect(jsonPath("$[1].mes").value("2026-02"));
    }

    @Test
    @DisplayName("sin datos para esa entidad devuelve lista vacía")
    void listaVacia() throws Exception {
      mockMvc.perform(get(BASE).param("entidad", "bbva"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(0));
    }
  }

  @Nested
  @DisplayName("PUT /banco/balances/{id}")
  class Actualizar {

    @Test
    @DisplayName("actualiza balance y aporte conservando entidad y mes")
    void actualizaBalanceYAporte() throws Exception {
      crear("bbva", "2026-04", 100.00, 30.00);

      mockMvc.perform(put(BASE + "/bbva-2026-04")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "entidad": "bbva",
                    "mes": "2026-04",
                    "balanceMensual": 123.45,
                    "aporteMensual": 40.00
                  }
                  """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("bbva-2026-04"))
          .andExpect(jsonPath("$.entidad").value("bbva"))
          .andExpect(jsonPath("$.mes").value("2026-04"))
          .andExpect(jsonPath("$.balanceMensual").value(123.45))
          .andExpect(jsonPath("$.aporteMensual").value(40.0));

      assertThat(countRows()).isEqualTo(1);
    }
  }

  @Nested
  @DisplayName("DELETE /banco/balances/{id}")
  class Eliminar {

    @Test
    @DisplayName("elimina la fila indicada")
    void elimina() throws Exception {
      crear("bbva", "2026-06", 10.00);
      assertThat(countRows()).isEqualTo(1);

      mockMvc.perform(delete(BASE + "/bbva-2026-06"))
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

  private void crear(String entidad, String mes, double balance) throws Exception {
    mockMvc.perform(post(BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"entidad":"%s","mes":"%s","balanceMensual":%s}
                """.formatted(entidad, mes, balance)))
        .andExpect(status().isCreated());
  }

  private void crear(String entidad, String mes, double balance, double aporte) throws Exception {
    mockMvc.perform(post(BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"entidad":"%s","mes":"%s","balanceMensual":%s,"aporteMensual":%s}
                """.formatted(entidad, mes, balance, aporte)))
        .andExpect(status().isCreated());
  }

  private int countRows() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM banco_balances", Integer.class);
  }
}