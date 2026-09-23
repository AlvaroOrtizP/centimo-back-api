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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/it/gastos/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Gastos — CRUD de gastos mensuales categorizados")
class GastoIT extends AbstractIntegrationIT {

  private static final String BASE = "/api/v1/expenses";
  private static final String ENTIDAD = "com.centimo.api.database.models.GastoMO";

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Nested
  @DisplayName("POST /expenses")
  class Crear {

    @Test
    @DisplayName("crea la fila con id UUID y persistencia del enum de categoría")
    void creaConIdUuidYCategoria() throws Exception {
      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "category": "Comida",
                    "amount": 20.00,
                    "date": "2026-07-02"
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").isNotEmpty())
          .andExpect(jsonPath("$.category").value("Comida"))
          .andExpect(jsonPath("$.amount").value(20.0))
          .andExpect(jsonPath("$.date").value("2026-07-02"));

      Map<String, Object> row = jdbcTemplate.queryForMap(
          "SELECT id, categoria, cantidad, fecha, fecha_creacion FROM gastos WHERE categoria = ?",
          "Comida");
      assertThat(row.get("id")).isNotNull();
      assertThat(row.get("categoria")).isEqualTo("Comida");
      assertThat((BigDecimal) row.get("cantidad")).isEqualByComparingTo("20.00");
      assertThat(java.sql.Date.valueOf("2026-07-02").toLocalDate()).isEqualTo(java.sql.Date.valueOf(row.get("fecha").toString()).toLocalDate());
      assertThat(row.get("fecha_creacion")).isNotNull();

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD)
          .hasInsertCount(1)
          .verify();
    }

    @Test
    @DisplayName("guarda la descripción opcional cuando se envía")
    void guardaDescripcionOpcional() throws Exception {
      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "category": "Ocio",
                    "amount": 6.23,
                    "date": "2026-07-13",
                    "description": "Suscripción"
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.description").value("Suscripción"));

      assertThat(jdbcTemplate.queryForObject(
          "SELECT descripcion FROM gastos", String.class))
          .isEqualTo("Suscripción");
    }
  }

  @Nested
  @DisplayName("GET /expenses")
  class Listar {

    @Test
    @DisplayName("filtra por año y mes y ordena por fecha descendente por defecto")
    void filtraPorPeriodoDescendente() throws Exception {
      crear("Comida", 20.00, "2026-07-02");
      crear("Ocio", 5.00, "2026-07-13");
      crear("Ocio", 6.23, "2026-07-29");
      crear("Trabajo", 10.00, "2026-08-01");

      mockMvc.perform(get(BASE).param("year", "2026").param("month", "7"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(3))
          .andExpect(jsonPath("$[0].date").value("2026-07-29"))
          .andExpect(jsonPath("$[1].date").value("2026-07-13"))
          .andExpect(jsonPath("$[2].date").value("2026-07-02"));
    }

    @Test
    @DisplayName("order=asc invierte el orden por fecha")
    void listaAscendente() throws Exception {
      crear("Comida", 20.00, "2026-07-02");
      crear("Ocio", 5.00, "2026-07-13");

      mockMvc.perform(get(BASE).param("year", "2026").param("month", "7").param("order", "asc"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[0].date").value("2026-07-02"))
          .andExpect(jsonPath("$[1].date").value("2026-07-13"));
    }

    @Test
    @DisplayName("filtra solo por año sin mes y devuelve el año completo")
    void filtraSoloPorAnio() throws Exception {
      crear("Comida", 20.00, "2026-07-02");
      crear("Ocio", 5.00, "2025-12-31");
      crear("Trabajo", 10.00, "2026-01-15");

      mockMvc.perform(get(BASE).param("year", "2026"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("sin filtros devuelve todos los gastos")
    void listaTodos() throws Exception {
      crear("Comida", 20.00, "2026-07-02");
      crear("Ocio", 5.00, "2026-07-13");

      mockMvc.perform(get(BASE))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("month sin year devuelve 400")
    void monthSinYearRechazado() throws Exception {
      mockMvc.perform(get(BASE).param("month", "7"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("periodo sin datos devuelve lista vacía")
    void periodoVacio() throws Exception {
      mockMvc.perform(get(BASE).param("year", "2020").param("month", "1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(0));
    }
  }

  @Nested
  @DisplayName("PUT /expenses/{id}")
  class Actualizar {

    @Test
    @DisplayName("actualiza importe, categoría y fecha conservando el id")
    void actualizaCampos() throws Exception {
      crear("Comida", 20.00, "2026-07-02");
      String id = jdbcTemplate.queryForObject(
          "SELECT id FROM gastos", String.class);

      mockMvc.perform(put(BASE + "/" + id)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "category": "Ocio",
                    "amount": 25.50,
                    "date": "2026-07-03"
                  }
                  """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(id))
          .andExpect(jsonPath("$.category").value("Ocio"))
          .andExpect(jsonPath("$.amount").value(25.5))
          .andExpect(jsonPath("$.date").value("2026-07-03"));

      assertThat(countRows()).isEqualTo(1);
      assertThat(jdbcTemplate.queryForObject(
          "SELECT categoria FROM gastos WHERE id = ?", String.class, id))
          .isEqualTo("Ocio");
      assertThat(jdbcTemplate.queryForObject(
          "SELECT cantidad FROM gastos WHERE id = ?", BigDecimal.class, id))
          .isEqualByComparingTo("25.50");

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD)
          .hasInsertCount(1)
          .hasUpdateCount(1)
          .hasLoadCount(1)
          .verify();
    }

    @Test
    @DisplayName("campos no enviados se conservan")
    void conservaCamposNoEnviados() throws Exception {
      crear("Comida", 20.00, "2026-07-02");
      String id = jdbcTemplate.queryForObject(
          "SELECT id FROM gastos", String.class);

      mockMvc.perform(put(BASE + "/" + id)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "amount": 30.00
                  }
                  """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.category").value("Comida"))
          .andExpect(jsonPath("$.amount").value(30.0))
          .andExpect(jsonPath("$.date").value("2026-07-02"));
    }
  }

  @Nested
  @DisplayName("DELETE /expenses/{id}")
  class Eliminar {

    @Test
    @DisplayName("elimina la fila indicada")
    void elimina() throws Exception {
      crear("Ocio", 5.00, "2026-07-13");
      String id = jdbcTemplate.queryForObject(
          "SELECT id FROM gastos", String.class);
      assertThat(countRows()).isEqualTo(1);

      mockMvc.perform(delete(BASE + "/" + id))
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

  private void crear(String category, double amount, String date) throws Exception {
    mockMvc.perform(post(BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"category":"%s","amount":%s,"date":"%s"}
                """.formatted(category, amount, date)))
        .andExpect(status().isCreated());
  }

  private int countRows() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM gastos", Integer.class);
  }
}