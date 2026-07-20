package com.centimo.api.it;

import com.centimo.api.it.support.StatisticsAssert;
import com.centimo.api.database.models.OperacionInversionMO;
import com.centimo.api.database.repositories.CuentaRepository;
import com.centimo.api.database.repositories.OperacionInversionRepository;
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
class OperacionInversionControllerIT extends AbstractIntegrationIT {

  private static final String PLATAFORMA_ID = "bbva-operaciones-it";
  private static final String CUENTA_ID = "bbva-operaciones-it-inversion";
  private static final String OPERACION_ID = "operacion-it-1";

  @Autowired PlataformaRepository plataformaRepository;
  @Autowired CuentaRepository cuentaRepository;
  @Autowired OperacionInversionRepository operacionRepository;

  @Test
  @Order(1)
  void setupBaseData() throws Exception {
    mockMvc.perform(post("/plataformas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id":"%s","name":"BBVA","type":"inversion","color":"#004481","icon":"building","order":1}
                """.formatted(PLATAFORMA_ID)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/cuentas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id":"%s","platformId":"%s","name":"Inversión","type":"inversion","currency":"EUR","order":1}
                """.formatted(CUENTA_ID, PLATAFORMA_ID)))
        .andExpect(status().isCreated());
  }

  @Test
  @Order(2)
  void crearOperacionCompra_returns201(CapturedOutput output) throws Exception {
    mockMvc.perform(post("/operaciones")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "%s",
                  "accountId": "%s",
                  "assetName": "Bitcoin",
                  "assetType": "cripto",
                  "type": "compra",
                  "buyDate": "2026-07-10",
                  "buyQuantity": 0.5,
                  "buyPricePerUnit": 60000.00,
                  "buyTotalCost": 30000.00,
                  "sellDate": null,
                  "sellPricePerUnit": null,
                  "sellTotalReceived": null,
                  "sellQuantity": null,
                  "pnl": null,
                  "status": "abierta"
                }
                """.formatted(OPERACION_ID, CUENTA_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(OPERACION_ID))
        .andExpect(jsonPath("$.accountId").value(CUENTA_ID))
        .andExpect(jsonPath("$.assetName").value("Bitcoin"))
        .andExpect(jsonPath("$.assetType").value("cripto"))
        .andExpect(jsonPath("$.type").value("compra"))
        .andExpect(jsonPath("$.buyDate").value("2026-07-10"))
        .andExpect(jsonPath("$.buyQuantity").value(0.5))
        .andExpect(jsonPath("$.buyPricePerUnit").value(60000.00))
        .andExpect(jsonPath("$.buyTotalCost").value(30000.00))
        .andExpect(jsonPath("$.status").value("abierta"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(OperacionInversionMO.class).hasInsertCount(1)
        .verify();
  }

  @Test
  @Order(3)
  void buscarTodas_returnsOperaciones(CapturedOutput output) throws Exception {
    mockMvc.perform(get("/operaciones")
            .param("cuentaId", CUENTA_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(OPERACION_ID))
        .andExpect(jsonPath("$[0].assetName").value("Bitcoin"))
        .andExpect(jsonPath("$[0].status").value("abierta"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(OperacionInversionMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(4)
  void eliminarOperacion_returns204(CapturedOutput output) throws Exception {
    mockMvc.perform(delete("/operaciones/" + OPERACION_ID))
        .andExpect(status().isNoContent());

    StatisticsAssert.assertThat(statistics())
        .forEntity(OperacionInversionMO.class).hasLoadCount(1).hasDeleteCount(1)
        .verify();
  }

  @Test
  @Order(5)
  void cleanup() throws Exception {
    mockMvc.perform(delete("/cuentas/" + CUENTA_ID)).andExpect(status().isNoContent());
    mockMvc.perform(delete("/plataformas/" + PLATAFORMA_ID)).andExpect(status().isNoContent());
  }
}
