package com.centimo.api.it;

import com.centimo.api.it.support.StatisticsAssert;
import com.centimo.api.database.models.FuenteIngresoMO;
import com.centimo.api.database.models.InstantaneaMensualMO;
import com.centimo.api.database.repositories.CuentaRepository;
import com.centimo.api.database.repositories.FuenteIngresoRepository;
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
class FuenteIngresoControllerIT extends AbstractIntegrationIT {

  private static final String PLATAFORMA_ID = "bbva-ingresos-it";
  private static final String CUENTA_ID = "bbva-ingresos-it-nomina";
  private static final String INSTANTANEA_ID = "bbva-ingresos-it-nomina-2026-07";
  private static final String INGRESO_ID = "ingreso-it-1";

  @Autowired PlataformaRepository plataformaRepository;
  @Autowired CuentaRepository cuentaRepository;
  @Autowired InstantaneaMensualRepository instantaneaRepository;
  @Autowired FuenteIngresoRepository fuenteIngresoRepository;

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
                  "expenses": 800.00,
                  "contribution": 0,
                  "notes": null
                }
                """.formatted(INSTANTANEA_ID, CUENTA_ID)))
        .andExpect(status().isCreated());
  }

  @Test
  @Order(2)
  void crearIngreso_incrementaIngresosInstantanea(CapturedOutput output) throws Exception {
    mockMvc.perform(post("/ingresos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "%s",
                  "snapshotId": "%s",
                  "source": "Nómina",
                  "description": "Salario julio",
                  "amount": 2200.00
                }
                """.formatted(INGRESO_ID, INSTANTANEA_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(INGRESO_ID))
        .andExpect(jsonPath("$.snapshotId").value(INSTANTANEA_ID))
        .andExpect(jsonPath("$.source").value("Nómina"))
        .andExpect(jsonPath("$.amount").value(2200.00));

    StatisticsAssert.assertThat(statistics())
        .forEntity(FuenteIngresoMO.class).hasInsertCount(1)
        .and(InstantaneaMensualMO.class).hasLoadCount(1).hasUpdateCount(1)
        .verify();
  }

  @Test
  @Order(3)
  void buscarTodas_returnsIngresos(CapturedOutput output) throws Exception {
    mockMvc.perform(get("/ingresos")
            .param("instantaneaId", INSTANTANEA_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(INGRESO_ID))
        .andExpect(jsonPath("$[0].source").value("Nómina"))
        .andExpect(jsonPath("$[0].amount").value(2200.00));

    StatisticsAssert.assertThat(statistics())
        .forEntity(FuenteIngresoMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(4)
  void eliminarIngreso_decrementaIngresosInstantanea(CapturedOutput output) throws Exception {
    mockMvc.perform(delete("/ingresos/" + INGRESO_ID))
        .andExpect(status().isNoContent());

    StatisticsAssert.assertThat(statistics())
        .forEntity(FuenteIngresoMO.class).hasLoadCount(1).hasDeleteCount(1)
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
