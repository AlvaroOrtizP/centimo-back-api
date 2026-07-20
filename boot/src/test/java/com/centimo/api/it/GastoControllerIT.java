package com.centimo.api.it;

import com.centimo.api.it.support.StatisticsAssert;
import com.centimo.api.database.models.GastoMO;
import com.centimo.api.database.models.InstantaneaMensualMO;
import com.centimo.api.database.repositories.CuentaRepository;
import com.centimo.api.database.repositories.GastoRepository;
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
class GastoControllerIT extends AbstractIntegrationIT {

  private static final String PLATAFORMA_ID = "bbva-gastos-it";
  private static final String CUENTA_ID = "bbva-gastos-it-nomina";
  private static final String INSTANTANEA_ID = "bbva-gastos-it-nomina-2026-07";
  private static final String GASTO_ID = "gasto-it-1";

  @Autowired PlataformaRepository plataformaRepository;
  @Autowired CuentaRepository cuentaRepository;
  @Autowired InstantaneaMensualRepository instantaneaRepository;
  @Autowired GastoRepository gastoRepository;

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
  void crearGasto_incrementaGastosInstantanea(CapturedOutput output) throws Exception {
    mockMvc.perform(post("/gastos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "%s",
                  "snapshotId": "%s",
                  "category": "comida",
                  "amount": 45.50,
                  "date": "2026-07-15",
                  "description": "Supermercado"
                }
                """.formatted(GASTO_ID, INSTANTANEA_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(GASTO_ID))
        .andExpect(jsonPath("$.snapshotId").value(INSTANTANEA_ID))
        .andExpect(jsonPath("$.category").value("comida"))
        .andExpect(jsonPath("$.amount").value(45.50))
        .andExpect(jsonPath("$.description").value("Supermercado"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(GastoMO.class).hasInsertCount(1)
        .and(InstantaneaMensualMO.class).hasLoadCount(1).hasUpdateCount(1)
        .verify();
  }

  @Test
  @Order(3)
  void buscarTodos_returnsGastos(CapturedOutput output) throws Exception {
    mockMvc.perform(get("/gastos")
            .param("instantaneaId", INSTANTANEA_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(GASTO_ID))
        .andExpect(jsonPath("$[0].category").value("comida"))
        .andExpect(jsonPath("$[0].amount").value(45.50));

    StatisticsAssert.assertThat(statistics())
        .forEntity(GastoMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(4)
  void eliminarGasto_decrementaGastosInstantanea(CapturedOutput output) throws Exception {
    mockMvc.perform(delete("/gastos/" + GASTO_ID))
        .andExpect(status().isNoContent());

    StatisticsAssert.assertThat(statistics())
        .forEntity(GastoMO.class).hasLoadCount(1).hasDeleteCount(1)
        .and(InstantaneaMensualMO.class).hasLoadCount(1).hasUpdateCount(1)
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
