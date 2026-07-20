package com.centimo.api.it;

import com.centimo.api.it.support.StatisticsAssert;
import com.centimo.api.database.models.PosicionInversionMO;
import com.centimo.api.database.repositories.CuentaRepository;
import com.centimo.api.database.repositories.InstantaneaMensualRepository;
import com.centimo.api.database.repositories.PlataformaRepository;
import com.centimo.api.database.repositories.PosicionInversionRepository;
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
class PosicionInversionControllerIT extends AbstractIntegrationIT {

  private static final String PLATAFORMA_ID = "bbva-posiciones-it";
  private static final String CUENTA_ID = "bbva-posiciones-it-nomina";
  private static final String INSTANTANEA_ID = "bbva-posiciones-it-nomina-2026-07";
  private static final String POSICION_ID = "posicion-it-1";

  @Autowired PlataformaRepository plataformaRepository;
  @Autowired CuentaRepository cuentaRepository;
  @Autowired InstantaneaMensualRepository instantaneaRepository;
  @Autowired PosicionInversionRepository posicionRepository;

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
                  "income": 2200.00,
                  "expenses": 0.00,
                  "contribution": 0,
                  "notes": null
                }
                """.formatted(INSTANTANEA_ID, CUENTA_ID)))
        .andExpect(status().isCreated());
  }

  @Test
  @Order(2)
  void crearPosicion_returns201(CapturedOutput output) throws Exception {
    mockMvc.perform(post("/posiciones")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "%s",
                  "snapshotId": "%s",
                  "assetName": "Bitcoin",
                  "assetType": "cripto",
                  "quantity": 0.5,
                  "valuePerUnit": 60000.00,
                  "totalValue": 30000.00
                }
                """.formatted(POSICION_ID, INSTANTANEA_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(POSICION_ID))
        .andExpect(jsonPath("$.snapshotId").value(INSTANTANEA_ID))
        .andExpect(jsonPath("$.assetName").value("Bitcoin"))
        .andExpect(jsonPath("$.assetType").value("cripto"))
        .andExpect(jsonPath("$.quantity").value(0.5))
        .andExpect(jsonPath("$.valuePerUnit").value(60000.00))
        .andExpect(jsonPath("$.totalValue").value(30000.00));

    StatisticsAssert.assertThat(statistics())
        .forEntity(PosicionInversionMO.class).hasInsertCount(1)
        .verify();
  }

  @Test
  @Order(3)
  void buscarTodas_returnsPosiciones(CapturedOutput output) throws Exception {
    mockMvc.perform(get("/posiciones")
            .param("instantaneaId", INSTANTANEA_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(POSICION_ID))
        .andExpect(jsonPath("$[0].assetName").value("Bitcoin"))
        .andExpect(jsonPath("$[0].totalValue").value(30000.00));

    StatisticsAssert.assertThat(statistics())
        .forEntity(PosicionInversionMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(4)
  void eliminarPosicion_returns204(CapturedOutput output) throws Exception {
    mockMvc.perform(delete("/posiciones/" + POSICION_ID))
        .andExpect(status().isNoContent());

    StatisticsAssert.assertThat(statistics())
        .forEntity(PosicionInversionMO.class).hasLoadCount(1).hasDeleteCount(1)
        .verify();
  }

  @Test
  @Order(5)
  void cleanup() throws Exception {
    mockMvc.perform(delete("/instantaneas/" + INSTANTANEA_ID)).andExpect(status().isNoContent());
    mockMvc.perform(delete("/cuentas/" + CUENTA_ID)).andExpect(status().isNoContent());
    mockMvc.perform(delete("/plataformas/" + PLATAFORMA_ID)).andExpect(status().isNoContent());
  }
}
