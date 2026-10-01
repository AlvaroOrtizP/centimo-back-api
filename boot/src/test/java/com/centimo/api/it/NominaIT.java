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

@Sql(scripts = "/it/nomina/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Nómina — ingreso del mes (clave natural mes)")
class NominaIT extends AbstractIntegrationIT {

  private static final String BASE = "/api/v1/nomina";
  private static final String ENTIDAD = "com.centimo.api.database.models.NominaMO";

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Nested
  @DisplayName("POST /nomina (upsert por mes)")
  class Crear {

    @Test
    @DisplayName("crea la fila con el mes como clave y persiste cantidad y nota")
    void creaConMesCantidadYNota() throws Exception {
      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026-07",
                    "cantidad": 1500.00,
                    "nota": "Nómina julio"
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.mes").value("2026-07"))
          .andExpect(jsonPath("$.cantidad").value(1500.0))
          .andExpect(jsonPath("$.nota").value("Nómina julio"));

      Map<String, Object> row = jdbcTemplate.queryForMap(
          "SELECT mes, cantidad, nota, fecha_creacion FROM nominas WHERE mes = ?",
          "2026-07");
      assertThat(row.get("nota")).isEqualTo("Nómina julio");
      assertThat((BigDecimal) row.get("cantidad")).isEqualByComparingTo("1500.00");
      assertThat(row.get("fecha_creacion")).isNotNull();

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD)
          .hasInsertCount(1)
          .verify();
    }

    @Test
    @DisplayName("repetir mes actualiza la fila sin duplicar y conserva fecha_creacion")
    void upsertNoDuplica() throws Exception {
      crear("2026-07", 1500.00, "Nómina julio");

      var fechaCreacionOriginal = jdbcTemplate.queryForObject(
          "SELECT fecha_creacion FROM nominas WHERE mes = ?", Object.class, "2026-07");

      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026-07",
                    "cantidad": 1600.00,
                    "nota": "Nómina julio (revisada)"
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.cantidad").value(1600.0))
          .andExpect(jsonPath("$.nota").value("Nómina julio (revisada)"));

      assertThat(countRows()).isEqualTo(1);
      assertThat(jdbcTemplate.queryForObject(
          "SELECT fecha_creacion FROM nominas WHERE mes = ?", Object.class, "2026-07"))
          .isEqualTo(fechaCreacionOriginal);
    }

    @Test
    @DisplayName("mes con formato inválido devuelve 400")
    void mesInvalidoDevuelve400() throws Exception {
      mockMvc.perform(post(BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026/07",
                    "cantidad": 1000.00
                  }
                  """))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("GET /nomina?mes=")
  class Obtener {

    @Test
    @DisplayName("devuelve el ingreso del mes indicado")
    void devuelveIngresoDelMes() throws Exception {
      crear("2026-07", 1500.00, "Nómina julio");

      mockMvc.perform(get(BASE).param("mes", "2026-07"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.mes").value("2026-07"))
          .andExpect(jsonPath("$.cantidad").value(1500.0))
          .andExpect(jsonPath("$.nota").value("Nómina julio"));
    }

    @Test
    @DisplayName("mes sin ingreso devuelve 404")
    void mesSinIngresoDevuelve404() throws Exception {
      mockMvc.perform(get(BASE).param("mes", "2026-08"))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("el parámetro mes es obligatorio (400)")
    void faltaParametroDevuelve400() throws Exception {
      mockMvc.perform(get(BASE))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("PUT /nomina/{mes}")
  class Actualizar {

    @Test
    @DisplayName("actualiza cantidad y nota del mes indicado")
    void actualizaCantidadYNota() throws Exception {
      crear("2026-07", 1500.00, "Nómina julio");

      mockMvc.perform(put(BASE + "/2026-07")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026-07",
                    "cantidad": 1550.00,
                    "nota": "Nómina julio + extras"
                  }
                  """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.mes").value("2026-07"))
          .andExpect(jsonPath("$.cantidad").value(1550.0))
          .andExpect(jsonPath("$.nota").value("Nómina julio + extras"));

      assertThat(jdbcTemplate.queryForObject(
          "SELECT cantidad FROM nominas WHERE mes = ?", BigDecimal.class, "2026-07"))
          .isEqualByComparingTo("1550.00");
    }

    @Test
    @DisplayName("mes sin ingreso devuelve 404")
    void mesInexistenteDevuelve404() throws Exception {
      mockMvc.perform(put(BASE + "/2026-09")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026-09",
                    "cantidad": 1000.00
                  }
                  """))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("DELETE /nomina/{mes}")
  class Eliminar {

    @Test
    @DisplayName("elimina el ingreso del mes indicado")
    void elimina() throws Exception {
      crear("2026-07", 1500.00, "Nómina julio");
      assertThat(countRows()).isEqualTo(1);

      mockMvc.perform(delete(BASE + "/2026-07"))
          .andExpect(status().isNoContent());

      assertThat(countRows()).isZero();
    }

    @Test
    @DisplayName("mes sin ingreso devuelve 404")
    void mesInexistenteDevuelve404() throws Exception {
      mockMvc.perform(delete(BASE + "/2026-10"))
          .andExpect(status().isNotFound());
    }
  }

  private void crear(String mes, double cantidad, String nota) throws Exception {
    mockMvc.perform(post(BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"mes":"%s","cantidad":%s,"nota":"%s"}
                """.formatted(mes, cantidad, nota)))
        .andExpect(status().isCreated());
  }

  private int countRows() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM nominas", Integer.class);
  }
}