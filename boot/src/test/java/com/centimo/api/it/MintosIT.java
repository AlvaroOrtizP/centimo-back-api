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

@Sql(scripts = "/it/mintos/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Mintos — CRUD de intereses mensuales")
class MintosIT extends AbstractIntegrationIT {

  private static final String BASE = "/api/v1/mintos/intereses-anuales";
  private static final String ENTIDAD = "com.centimo.api.database.models.InteresAnualMintosMO";

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Nested
  @DisplayName("POST /mintos/intereses-anuales (upsert por mes)")
  class Crear {

    @Test
    @DisplayName("crea la fila con id UUID y aplica el default de importe añadido")
    void creaConIdUuidYDefault() throws Exception {
      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026-01",
                    "valorFinal": 1000.00
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").isNotEmpty())
          .andExpect(jsonPath("$.mes").value("2026-01"))
          .andExpect(jsonPath("$.importeAñadido").value(0.0))
          .andExpect(jsonPath("$.valorFinal").value(1000.0));

      Map<String, Object> row = jdbcTemplate.queryForMap(
          "SELECT id, mes, importe_añadido, valor_final FROM mintos WHERE mes = ?",
          "2026-01");
      assertThat(row.get("id")).isNotNull();
      assertThat(row.get("mes")).isEqualTo("2026-01");
      assertThat((BigDecimal) row.get("importe_añadido")).isEqualByComparingTo("0.00");
      assertThat((BigDecimal) row.get("valor_final")).isEqualByComparingTo("1000.00");

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD)
          .hasInsertCount(1)
          .verify();
    }

    @Test
    @DisplayName("respeta los campos enviados")
    void respetaCampos() throws Exception {
      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026-02",
                    "importeAñadido": 200.00,
                    "valorFinal": 2500.50
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.importeAñadido").value(200.0))
          .andExpect(jsonPath("$.valorFinal").value(2500.5));
    }

    @Test
    @DisplayName("repetir mes actualiza la fila sin duplicar y conserva fecha_creacion")
    void upsertNoDuplica() throws Exception {
      crear("2026-03", 100.00, 500.00);

      var fechaCreacionOriginal = jdbcTemplate.queryForObject(
          "SELECT fecha_creacion FROM mintos WHERE mes = ?", Object.class, "2026-03");

      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026-03",
                    "importeAñadido": 75.00,
                    "valorFinal": 620.00
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.importeAñadido").value(75.0))
          .andExpect(jsonPath("$.valorFinal").value(620.0));

      assertThat(countRows()).isEqualTo(1);
      assertThat(jdbcTemplate.queryForObject(
          "SELECT valor_final FROM mintos WHERE mes = ?", BigDecimal.class, "2026-03"))
          .isEqualByComparingTo("620.00");
      assertThat(jdbcTemplate.queryForObject(
          "SELECT fecha_creacion FROM mintos WHERE mes = ?", Object.class, "2026-03"))
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
  @DisplayName("GET /mintos/intereses-anuales")
  class Listar {

    @Test
    @DisplayName("por defecto devuelve todos los intereses ordenados por mes descendente")
    void listaDescendentePorDefecto() throws Exception {
      crear("2025-11", 1.00, 10.00);
      crear("2025-12", 2.00, 20.00);
      crear("2026-01", 3.00, 30.00);

      mockMvc.perform(get(BASE))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(3))
          .andExpect(jsonPath("$[0].mes").value("2026-01"))
          .andExpect(jsonPath("$[1].mes").value("2025-12"))
          .andExpect(jsonPath("$[2].mes").value("2025-11"));
    }

    @Test
    @DisplayName("filtra por mes y devuelve solo ese mes")
    void filtraPorMes() throws Exception {
      crear("2026-01", 1.00, 10.00);
      crear("2026-02", 2.00, 20.00);

      mockMvc.perform(get(BASE).param("mes", "2026-02"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0].mes").value("2026-02"))
          .andExpect(jsonPath("$[0].importeAñadido").value(2.0));

      mockMvc.perform(get(BASE).param("mes", "2026-03"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(0));
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
  @DisplayName("PUT /mintos/intereses-anuales/{id}")
  class Actualizar {

    @Test
    @DisplayName("actualiza importe añadido y valor final sin tocar el mes")
    void actualizaSinTocarElMes() throws Exception {
      crear("2026-04", 100.00, 1000.00);
      String id = jdbcTemplate.queryForObject(
          "SELECT id FROM mintos WHERE mes = ?", String.class, "2026-04");

      mockMvc.perform(put(BASE + "/" + id)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026-05",
                    "importeAñadido": 150.00,
                    "valorFinal": 1200.00
                  }
                  """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(id))
          .andExpect(jsonPath("$.importeAñadido").value(150.0))
          .andExpect(jsonPath("$.valorFinal").value(1200.0));

      assertThat(countRows()).isEqualTo(1);
      assertThat(jdbcTemplate.queryForObject(
          "SELECT mes FROM mintos WHERE id = ?", String.class, id))
          .isEqualTo("2026-04");

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD)
          .hasInsertCount(1)
          .hasUpdateCount(1)
          .hasLoadCount(1)
          .verify();
    }
  }

  @Nested
  @DisplayName("DELETE /mintos/intereses-anuales/{id}")
  class Eliminar {

    @Test
    @DisplayName("elimina la fila indicada")
    void elimina() throws Exception {
      crear("2026-06", 10.00, 100.00);
      String id = jdbcTemplate.queryForObject(
          "SELECT id FROM mintos WHERE mes = ?", String.class, "2026-06");
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

  private void crear(String mes, double importeAñadido, double valorFinal) throws Exception {
    mockMvc.perform(post(BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"mes":"%s","importeAñadido":%s,"valorFinal":%s}
                """.formatted(mes, importeAñadido, valorFinal)))
        .andExpect(status().isCreated());
  }

  private int countRows() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM mintos", Integer.class);
  }
}