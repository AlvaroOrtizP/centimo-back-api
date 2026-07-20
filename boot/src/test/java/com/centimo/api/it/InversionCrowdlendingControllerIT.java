package com.centimo.api.it;

import com.centimo.api.it.support.StatisticsAssert;
import com.centimo.api.database.models.InversionCrowdlendingMO;
import com.centimo.api.database.repositories.InversionCrowdlendingRepository;
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
class InversionCrowdlendingControllerIT extends AbstractIntegrationIT {

  private static final String PLATAFORMA_ID = "equito-crowdlending-it";
  private static final String CROWD_ID = "crowdlending-it-1";

  @Autowired PlataformaRepository plataformaRepository;
  @Autowired InversionCrowdlendingRepository inversionRepository;

  @Test
  @Order(1)
  void setupBaseData() throws Exception {
    mockMvc.perform(post("/plataformas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id":"%s","name":"Equito","type":"crowdlending","color":"#FF6B35","icon":"home","order":1}
                """.formatted(PLATAFORMA_ID)))
        .andExpect(status().isCreated());
  }

  @Test
  @Order(2)
  void crearInversion_returns201(CapturedOutput output) throws Exception {
    mockMvc.perform(post("/crowdlending")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "%s",
                  "platformId": "%s",
                  "projectName": "Piso en Valencia",
                  "investedAmount": 5000.00,
                  "interestRate": 8.5,
                  "termMonths": 24,
                  "startDate": "2026-01-15",
                  "endDate": null,
                  "monthlyReturn": 35.42,
                  "totalReturned": 212.50,
                  "status": "activo"
                }
                """.formatted(CROWD_ID, PLATAFORMA_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(CROWD_ID))
        .andExpect(jsonPath("$.platformId").value(PLATAFORMA_ID))
        .andExpect(jsonPath("$.projectName").value("Piso en Valencia"))
        .andExpect(jsonPath("$.investedAmount").value(5000.00))
        .andExpect(jsonPath("$.interestRate").value(8.5))
        .andExpect(jsonPath("$.status").value("activo"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(InversionCrowdlendingMO.class).hasInsertCount(1)
        .verify();
  }

  @Test
  @Order(3)
  void buscarTodas_returnsInversiones(CapturedOutput output) throws Exception {
    mockMvc.perform(get("/crowdlending")
            .param("plataformaId", PLATAFORMA_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(CROWD_ID))
        .andExpect(jsonPath("$[0].projectName").value("Piso en Valencia"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(InversionCrowdlendingMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(4)
  void eliminarInversion_returns204(CapturedOutput output) throws Exception {
    mockMvc.perform(delete("/crowdlending/" + CROWD_ID))
        .andExpect(status().isNoContent());

    StatisticsAssert.assertThat(statistics())
        .forEntity(InversionCrowdlendingMO.class).hasLoadCount(1).hasDeleteCount(1)
        .verify();
  }

  @Test
  @Order(5)
  void cleanup() throws Exception {
    mockMvc.perform(delete("/plataformas/" + PLATAFORMA_ID)).andExpect(status().isNoContent());
  }
}
