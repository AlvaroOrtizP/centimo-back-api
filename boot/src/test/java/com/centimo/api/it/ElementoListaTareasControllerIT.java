package com.centimo.api.it;

import com.centimo.api.it.support.StatisticsAssert;
import com.centimo.api.database.models.ElementoListaTareasMO;
import com.centimo.api.database.repositories.CuentaRepository;
import com.centimo.api.database.repositories.ElementoListaTareasRepository;
import com.centimo.api.database.repositories.InstantaneaMensualRepository;
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
class ElementoListaTareasControllerIT extends AbstractIntegrationIT {

  private static final String PLATAFORMA_ID = "bbva-tareas-it";
  private static final String CUENTA_ID = "bbva-tareas-it-nomina";
  private static final String INSTANTANEA_ID = "bbva-tareas-it-nomina-2026-07";

  @Autowired PlataformaRepository plataformaRepository;
  @Autowired CuentaRepository cuentaRepository;
  @Autowired InstantaneaMensualRepository instantaneaRepository;
  @Autowired ElementoListaTareasRepository elementoRepository;

  @Test
  @Order(1)
  void setupBaseData() throws Exception {
    mockMvc.perform(post("/plataformas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id":"%s","name":"BBVA","type":"banco","color":"#004481","icon":"building","order":1}
                """.formatted(PLATAFORMA_ID)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/cuentas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id":"%s","platformId":"%s","name":"Nómina","type":"corriente","currency":"EUR","order":1}
                """.formatted(CUENTA_ID, PLATAFORMA_ID)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/instantaneas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "%s",
                  "accountId": "%s",
                  "year": 2026,
                  "month": 7,
                  "balance": 5000.00,
                  "income": 0.00,
                  "expenses": 0.00,
                  "contribution": 0,
                  "notes": null
                }
                """.formatted(INSTANTANEA_ID, CUENTA_ID)))
        .andExpect(status().isCreated());
  }

  @Test
  @Order(2)
  void crearTarea_returns201(CapturedOutput output) throws Exception {
    mockMvc.perform(post("/instantaneas/" + INSTANTANEA_ID + "/tareas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"text": "Revisar inversiones"}
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.text").value("Revisar inversiones"))
        .andExpect(jsonPath("$.checked").value(false));

    StatisticsAssert.assertThat(statistics())
        .forEntity(ElementoListaTareasMO.class).hasInsertCount(1)
        .verify();
  }

  @Test
  @Order(3)
  void crearSegundaTarea_seAñadeAlFinal(CapturedOutput output) throws Exception {
    mockMvc.perform(post("/instantaneas/" + INSTANTANEA_ID + "/tareas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"text": "Pagar luz"}
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.text").value("Pagar luz"));

    mockMvc.perform(get("/instantaneas/" + INSTANTANEA_ID + "/tareas"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @Order(4)
  void alternarTarea_returnsCheckedTrue(CapturedOutput output) throws Exception {
    String elementoId = elementoRepository.findByInstantaneaIdOrderByOrden(INSTANTANEA_ID).get(0).getId();

    mockMvc.perform(post("/instantaneas/" + INSTANTANEA_ID + "/tareas/" + elementoId + "/alternar"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.checked").value(true));

    StatisticsAssert.assertThat(statistics())
        .forEntity(ElementoListaTareasMO.class).hasLoadCount(3).hasUpdateCount(1)
        .verify();
  }

  @Test
  @Order(5)
  void alternarTareaVuelveAFalso(CapturedOutput output) throws Exception {
    String elementoId = elementoRepository.findByInstantaneaIdOrderByOrden(INSTANTANEA_ID).get(0).getId();

    mockMvc.perform(post("/instantaneas/" + INSTANTANEA_ID + "/tareas/" + elementoId + "/alternar"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.checked").value(false));

    StatisticsAssert.assertThat(statistics())
        .forEntity(ElementoListaTareasMO.class).hasLoadCount(3).hasUpdateCount(1)
        .verify();
  }

  @Test
  @Order(6)
  void cleanup() throws Exception {
    elementoRepository.deleteAllInBatch();
    mockMvc.perform(delete("/instantaneas/" + INSTANTANEA_ID)).andExpect(status().isNoContent());
    mockMvc.perform(delete("/cuentas/" + CUENTA_ID)).andExpect(status().isNoContent());
    mockMvc.perform(delete("/plataformas/" + PLATAFORMA_ID)).andExpect(status().isNoContent());
  }
}
