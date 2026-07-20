package com.centimo.api.it;

import com.centimo.api.it.support.StatisticsAssert;
import com.centimo.api.database.models.AsignacionSalarioMO;
import com.centimo.api.database.repositories.AsignacionSalarioRepository;
import com.centimo.api.database.repositories.PlataformaRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AsignacionSalarioControllerIT extends AbstractIntegrationIT {

  private static final String PLATAFORMA_ID = "b100-salario-it";
  private static final String ASIG_ID = "asig-salario-it-1";

  @Autowired PlataformaRepository plataformaRepository;
  @Autowired AsignacionSalarioRepository asignacionRepository;

  @Test
  @Order(1)
  void setupBaseData() throws Exception {
    mockMvc.perform(post("/plataformas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id":"%s","name":"B100","type":"banco","color":"#6C3FD1","icon":"smartphone","order":1}
                """.formatted(PLATAFORMA_ID)))
        .andExpect(status().isCreated());
  }

  @Test
  @Order(2)
  void crearAsignacion_returns201(CapturedOutput output) throws Exception {
    mockMvc.perform(post("/asignaciones-salario")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "%s",
                  "year": 2026,
                  "month": 7,
                  "platformId": "%s",
                  "type": "fijo",
                  "value": 100.00,
                  "note": "Ahorro mensual"
                }
                """.formatted(ASIG_ID, PLATAFORMA_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(ASIG_ID))
        .andExpect(jsonPath("$.year").value(2026))
        .andExpect(jsonPath("$.month").value(7))
        .andExpect(jsonPath("$.platformId").value(PLATAFORMA_ID))
        .andExpect(jsonPath("$.type").value("fijo"))
        .andExpect(jsonPath("$.value").value(100.00))
        .andExpect(jsonPath("$.note").value("Ahorro mensual"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(AsignacionSalarioMO.class).hasInsertCount(1)
        .verify();
  }

  @Test
  @Order(3)
  void buscarTodasConFiltro_returnsAsignaciones(CapturedOutput output) throws Exception {
    mockMvc.perform(get("/asignaciones-salario")
            .param("anio", "2026")
            .param("mes", "7"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(ASIG_ID))
        .andExpect(jsonPath("$[0].value").value(100.00));

    StatisticsAssert.assertThat(statistics())
        .forEntity(AsignacionSalarioMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(4)
  void actualizarAsignacion_returns200(CapturedOutput output) throws Exception {
    mockMvc.perform(put("/asignaciones-salario/" + ASIG_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "%s",
                  "year": 2026,
                  "month": 7,
                  "platformId": "%s",
                  "type": "fijo",
                  "value": 150.00,
                  "note": "Ahorro aumentado"
                }
                """.formatted(ASIG_ID, PLATAFORMA_ID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.value").value(150.00))
        .andExpect(jsonPath("$.note").value("Ahorro aumentado"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(AsignacionSalarioMO.class).hasLoadCount(1).hasUpdateCount(1)
        .verify();
  }

  @Test
  @Order(5)
  void eliminarAsignacion_returns204(CapturedOutput output) throws Exception {
    mockMvc.perform(delete("/asignaciones-salario/" + ASIG_ID))
        .andExpect(status().isNoContent());

    StatisticsAssert.assertThat(statistics())
        .forEntity(AsignacionSalarioMO.class).hasLoadCount(1).hasDeleteCount(1)
        .verify();
  }

  @Test
  @Order(6)
  void cleanup() throws Exception {
    mockMvc.perform(delete("/plataformas/" + PLATAFORMA_ID)).andExpect(status().isNoContent());
  }
}
