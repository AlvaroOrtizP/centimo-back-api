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

@Sql(scripts = "/it/revolut/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Revolut — CRUD de balances mensuales")
class RevolutIT extends AbstractIntegrationIT {

  private static final String BASE = "/api/v1/revolut-balances";
  private static final String ENTIDAD = "com.centimo.api.database.models.RevolutBalanceMO";

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Nested
  @DisplayName("POST /revolut-balances (upsert por mes)")
  class Crear {

    @Test
    @DisplayName("crea la fila con id natural {AAAA-MM} y aplica defaults en campos opcionales")
    void creaConIdNaturalYDefaults() throws Exception {
      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026-01",
                    "balanceMensual": 100.50
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("2026-01"))
          .andExpect(jsonPath("$.mes").value("2026-01"))
          .andExpect(jsonPath("$.balanceMensual").value(100.5))
          .andExpect(jsonPath("$.aporteMensual").value(0.0))
          .andExpect(jsonPath("$.dineroTotal").value(0.0))
          .andExpect(jsonPath("$.dineroHacienda").value(0.0))
          .andExpect(jsonPath("$.dineroFinal").value(0.0));

      var row = jdbcTemplate.queryForMap(
          "SELECT mes, balance_mensual, aporte_mensual, dinero_total, dinero_hacienda, dinero_final "
              + "FROM revolut_balances WHERE id = ?",
          "2026-01");
      assertThat(row.get("mes")).isEqualTo("2026-01");
      assertThat((BigDecimal) row.get("balance_mensual")).isEqualByComparingTo("100.50");
      assertThat((BigDecimal) row.get("aporte_mensual")).isEqualByComparingTo("0.00");
      assertThat((BigDecimal) row.get("dinero_total")).isEqualByComparingTo("0.00");
      assertThat((BigDecimal) row.get("dinero_hacienda")).isEqualByComparingTo("0.00");
      assertThat((BigDecimal) row.get("dinero_final")).isEqualByComparingTo("0.00");

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
                    "mes": "2026-02",
                    "balanceMensual": 1000.00,
                    "aporteMensual": 200.00,
                    "dineroTotal": 1.50,
                    "dineroHacienda": 0.29,
                    "dineroFinal": 1.21
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("2026-02"))
          .andExpect(jsonPath("$.aporteMensual").value(200.0))
          .andExpect(jsonPath("$.dineroTotal").value(1.5))
          .andExpect(jsonPath("$.dineroHacienda").value(0.29))
          .andExpect(jsonPath("$.dineroFinal").value(1.21));
    }

    @Test
    @DisplayName("repetir mes actualiza la fila sin duplicar y conserva fecha_creacion")
    void upsertNoDuplica() throws Exception {
      crear("2026-03", 10.00);

      var fechaCreacionOriginal = jdbcTemplate.queryForObject(
          "SELECT fecha_creacion FROM revolut_balances WHERE id = ?", Object.class, "2026-03");

      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026-03",
                    "balanceMensual": 99.99,
                    "aporteMensual": 25.00
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("2026-03"))
          .andExpect(jsonPath("$.balanceMensual").value(99.99))
          .andExpect(jsonPath("$.aporteMensual").value(25.0));

      assertThat(countRows()).isEqualTo(1);
      assertThat(jdbcTemplate.queryForObject(
          "SELECT balance_mensual FROM revolut_balances WHERE id = ?", BigDecimal.class, "2026-03"))
          .isEqualByComparingTo("99.99");
      assertThat(jdbcTemplate.queryForObject(
          "SELECT fecha_creacion FROM revolut_balances WHERE id = ?", Object.class, "2026-03"))
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
  @DisplayName("GET /revolut-balances")
  class Listar {

    @Test
    @DisplayName("por defecto devuelve todos los balances ordenados por mes descendente")
    void listaDescendentePorDefecto() throws Exception {
      crear("2025-11", 1.00);
      crear("2025-12", 2.00);
      crear("2026-01", 3.00);

      mockMvc.perform(get(BASE))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(3))
          .andExpect(jsonPath("$[0].mes").value("2026-01"))
          .andExpect(jsonPath("$[1].mes").value("2025-12"))
          .andExpect(jsonPath("$[2].mes").value("2025-11"));
    }

    @Test
    @DisplayName("order=asc invierte el orden y limit recorta el resultado")
    void listaAscendenteConLimite() throws Exception {
      crear("2025-11", 1.00);
      crear("2025-12", 2.00);
      crear("2026-01", 3.00);

      mockMvc.perform(get(BASE).param("order", "asc").param("limit", "2"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[0].mes").value("2025-11"))
          .andExpect(jsonPath("$[1].mes").value("2025-12"));
    }

    @Test
    @DisplayName("sin datos devuelve lista vacía")
    void listaVacia() throws Exception {
      mockMvc.perform(get(BASE))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(0));
    }
  }

  @Nested
  @DisplayName("PUT /revolut-balances/{id}")
  class Actualizar {

    @Test
    @DisplayName("actualiza los campos enviados y conserva los opcionales omitidos")
    void actualizaCamposEnviados() throws Exception {
      crear("2026-04", 100.00, 200.00, 1.50, 0.29, 1.21);

      mockMvc.perform(put(BASE + "/2026-04")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026-04",
                    "balanceMensual": 123.45
                  }
                  """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("2026-04"))
          .andExpect(jsonPath("$.balanceMensual").value(123.45))
          .andExpect(jsonPath("$.aporteMensual").value(200.0))
          .andExpect(jsonPath("$.dineroTotal").value(1.5))
          .andExpect(jsonPath("$.dineroHacienda").value(0.29))
          .andExpect(jsonPath("$.dineroFinal").value(1.21));

      assertThat(countRows()).isEqualTo(1);
    }
  }

  @Nested
  @DisplayName("DELETE /revolut-balances/{id}")
  class Eliminar {

    @Test
    @DisplayName("elimina la fila indicada")
    void elimina() throws Exception {
      crear("2026-06", 10.00);
      assertThat(countRows()).isEqualTo(1);

      mockMvc.perform(delete(BASE + "/2026-06"))
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

  private void crear(String mes, double balance) throws Exception {
    mockMvc.perform(post(BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"mes":"%s","balanceMensual":%s}
                """.formatted(mes, balance)))
        .andExpect(status().isCreated());
  }

  private void crear(String mes, double balance, double aporte, double total, double hacienda, double dineroFinal) throws Exception {
    mockMvc.perform(post(BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"mes":"%s","balanceMensual":%s,"aporteMensual":%s,"dineroTotal":%s,
                 "dineroHacienda":%s,"dineroFinal":%s}
                """.formatted(mes, balance, aporte, total, hacienda, dineroFinal)))
        .andExpect(status().isCreated());
  }

  private int countRows() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM revolut_balances", Integer.class);
  }
}