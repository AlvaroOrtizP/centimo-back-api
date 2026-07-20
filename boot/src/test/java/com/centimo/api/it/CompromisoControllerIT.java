package com.centimo.api.it;

import com.centimo.api.it.support.StatisticsAssert;
import com.centimo.api.database.models.CompromisoMO;
import com.centimo.api.database.repositories.CompromisoRepository;
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
class CompromisoControllerIT extends AbstractIntegrationIT {

  private static final String COMPROMISO_ID = "compromiso-it-1";
  private static final String COMPROMISO_ANUAL_ID = "compromiso-it-anual";

  @Autowired CompromisoRepository compromisoRepository;

  @Test
  @Order(1)
  void crearCompromisoMensual_returns201(CapturedOutput output) throws Exception {
    mockMvc.perform(post("/compromisos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "%s",
                  "description": "Netflix",
                  "month": 0,
                  "type": "mensual",
                  "category": "suscripciones",
                  "amount": 15.99,
                  "isEstimated": false
                }
                """.formatted(COMPROMISO_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(COMPROMISO_ID))
        .andExpect(jsonPath("$.description").value("Netflix"))
        .andExpect(jsonPath("$.type").value("mensual"))
        .andExpect(jsonPath("$.amount").value(15.99));

    StatisticsAssert.assertThat(statistics())
        .forEntity(CompromisoMO.class).hasInsertCount(1)
        .verify();
  }

  @Test
  @Order(2)
  void crearCompromisoAnual_returns201(CapturedOutput output) throws Exception {
    mockMvc.perform(post("/compromisos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "%s",
                  "description": "IBI",
                  "month": 9,
                  "type": "anual",
                  "category": "impuestos",
                  "amount": 350.00,
                  "isEstimated": true
                }
                """.formatted(COMPROMISO_ANUAL_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(COMPROMISO_ANUAL_ID))
        .andExpect(jsonPath("$.type").value("anual"))
        .andExpect(jsonPath("$.isEstimated").value(true));
  }

  @Test
  @Order(3)
  void buscarTodos_returnsCompromisos(CapturedOutput output) throws Exception {
    mockMvc.perform(get("/compromisos"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));

    StatisticsAssert.assertThat(statistics())
        .forEntity(CompromisoMO.class).hasLoadCount(2)
        .verify();
  }

  @Test
  @Order(4)
  void buscarPorId_returnsCompromiso(CapturedOutput output) throws Exception {
    mockMvc.perform(get("/compromisos/" + COMPROMISO_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.description").value("Netflix"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(CompromisoMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(5)
  void alertas_returnsAlertasMesActual(CapturedOutput output) throws Exception {
    mockMvc.perform(get("/compromisos/alertas")
            .param("anio", "2026")
            .param("mes", "7"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].year").value(2026))
        .andExpect(jsonPath("$[0].month").value(7))
        .andExpect(jsonPath("$[0].compromisos[?(@.description=='Netflix')]").exists())
        .andExpect(jsonPath("$[0].total").value(15.99))
        .andExpect(jsonPath("$.length()").value(4));
  }

  @Test
  @Order(6)
  void eliminarCompromiso_returns204(CapturedOutput output) throws Exception {
    mockMvc.perform(delete("/compromisos/" + COMPROMISO_ID))
        .andExpect(status().isNoContent());

    mockMvc.perform(delete("/compromisos/" + COMPROMISO_ANUAL_ID))
        .andExpect(status().isNoContent());

    StatisticsAssert.assertThat(statistics())
        .forEntity(CompromisoMO.class).hasLoadCount(2).hasDeleteCount(2)
        .verify();
  }
}
