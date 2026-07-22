package com.centimo.api.it;

import com.centimo.api.it.support.StatisticsAssert;
import com.centimo.api.database.models.CuentaMO;
import com.centimo.api.database.models.InstantaneaMensualMO;
import com.centimo.api.database.models.PlataformaMO;
import com.centimo.api.database.repositories.CuentaRepository;
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

//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InstantaneaControllerIT extends AbstractIntegrationIT {

  private static final String PLATAFORMA_ID = "bbva-it";
  private static final String CUENTA_ID = "bbva-it-nomina";
  private static final String INSTANTANEA_ID = "bbva-it-nomina-2026-07";

  @Autowired
  PlataformaRepository plataformaRepository;

  @Autowired
  CuentaRepository cuentaRepository;

  @Autowired
  InstantaneaMensualRepository instantaneaRepository;

  /*@Test
  @Order(1)
  void crearPlataformaBase(CapturedOutput output) throws Exception {
    mockMvc.perform(post("/plataformas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id":"%s","name":"BBVA","type":"banco","color":"#004481","icon":"building","order":1}
                """.formatted(PLATAFORMA_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(PLATAFORMA_ID));

    StatisticsAssert.assertThat(statistics())
        .forEntity(PlataformaMO.class).hasInsertCount(1)
        .verify();
  }

  @Test
  @Order(2)
  void crearCuentaBase(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(post("/cuentas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id":"%s","platformId":"%s","name":"Nómina","type":"corriente","currency":"EUR","order":1}
                """.formatted(CUENTA_ID, PLATAFORMA_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(CUENTA_ID));

    StatisticsAssert.assertThat(statistics())
        .forEntity(CuentaMO.class).hasInsertCount(1)
        .verify();
  }

  @Test
  @Order(3)
  void crearInstantanea_returns201(CapturedOutput output) throws Exception {
    statistics().clear();

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
                  "expenses": 800.00,
                  "contribution": 100.00,
                  "notes": "Julio cerrado"
                }
                """.formatted(INSTANTANEA_ID, CUENTA_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(INSTANTANEA_ID))
        .andExpect(jsonPath("$.accountId").value(CUENTA_ID))
        .andExpect(jsonPath("$.year").value(2026))
        .andExpect(jsonPath("$.month").value(7))
        .andExpect(jsonPath("$.balance").value(5000.00))
        .andExpect(jsonPath("$.income").value(2200.00))
        .andExpect(jsonPath("$.expenses").value(800.00))
        .andExpect(jsonPath("$.contribution").value(100.00))
        .andExpect(jsonPath("$.notes").value("Julio cerrado"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(InstantaneaMensualMO.class).hasInsertCount(1)
        .verify();
  }

  @Test
  @Order(4)
  void buscarTodas_returnsInstantaneas(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(get("/instantaneas"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(INSTANTANEA_ID))
        .andExpect(jsonPath("$[0].accountId").value(CUENTA_ID));

    StatisticsAssert.assertThat(statistics())
        .forEntity(InstantaneaMensualMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(5)
  void buscarTodasConFiltrosAnioMes(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(get("/instantaneas")
            .param("anio", "2026")
            .param("mes", "7"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].year").value(2026))
        .andExpect(jsonPath("$[0].month").value(7));

    StatisticsAssert.assertThat(statistics())
        .forEntity(InstantaneaMensualMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(6)
  void buscarTodasConFiltroCuentaId(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(get("/instantaneas")
            .param("cuentaId", CUENTA_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].accountId").value(CUENTA_ID));

    StatisticsAssert.assertThat(statistics())
        .forEntity(InstantaneaMensualMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(7)
  void buscarTodasConTodosLosFiltros(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(get("/instantaneas")
            .param("anio", "2026")
            .param("mes", "7")
            .param("cuentaId", CUENTA_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(INSTANTANEA_ID));

    StatisticsAssert.assertThat(statistics())
        .forEntity(InstantaneaMensualMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(8)
  void buscarPorId_returnsInstantanea(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(get("/instantaneas/" + INSTANTANEA_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(INSTANTANEA_ID))
        .andExpect(jsonPath("$.accountId").value(CUENTA_ID))
        .andExpect(jsonPath("$.balance").value(5000.00));

    StatisticsAssert.assertThat(statistics())
        .forEntity(InstantaneaMensualMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(9)
  void upsert_creaNuevaInstantanea(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(post("/instantaneas/upsert")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "accountId": "%s",
                  "year": 2026,
                  "month": 6,
                  "balance": 4500.00,
                  "deltaIncome": 2200.00,
                  "expenses": 750.00
                }
                """.formatted(CUENTA_ID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountId").value(CUENTA_ID))
        .andExpect(jsonPath("$.year").value(2026))
        .andExpect(jsonPath("$.month").value(6))
        .andExpect(jsonPath("$.balance").value(4500.00))
        .andExpect(jsonPath("$.income").value(2200.00))
        .andExpect(jsonPath("$.expenses").value(750.00));

    StatisticsAssert.assertThat(statistics())
        .forEntity(InstantaneaMensualMO.class).hasInsertCount(1)
        .verify();
  }

  @Test
  @Order(10)
  void upsert_actualizaInstantaneaExistente(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(post("/instantaneas/upsert")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "accountId": "%s",
                  "year": 2026,
                  "month": 7,
                  "balance": 5200.00,
                  "deltaIncome": 300.00,
                  "expenses": 850.00
                }
                """.formatted(CUENTA_ID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(INSTANTANEA_ID))
        .andExpect(jsonPath("$.balance").value(5200.00))
        .andExpect(jsonPath("$.income").value(2500.00))
        .andExpect(jsonPath("$.expenses").value(850.00));

    StatisticsAssert.assertThat(statistics())
        .forEntity(InstantaneaMensualMO.class).hasLoadCount(1).hasUpdateCount(1)
        .verify();
  }

  @Test
  @Order(11)
  void upsert_deltaIncomeCeroNoModificaIngresos(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(post("/instantaneas/upsert")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "accountId": "%s",
                  "year": 2026,
                  "month": 7,
                  "balance": 5300.00,
                  "deltaIncome": 0,
                  "expenses": 900.00
                }
                """.formatted(CUENTA_ID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.income").value(2500.00))
        .andExpect(jsonPath("$.balance").value(5300.00))
        .andExpect(jsonPath("$.expenses").value(900.00));

    StatisticsAssert.assertThat(statistics())
        .forEntity(InstantaneaMensualMO.class).hasLoadCount(1).hasUpdateCount(1)
        .verify();
  }

  @Test
  @Order(12)
  void actualizarInstantanea_returns200(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(put("/instantaneas/" + INSTANTANEA_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "balance": 5500.00,
                  "income": 2600.00,
                  "expenses": 950.00,
                  "contribution": 200.00,
                  "notes": "Julio actualizado"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(INSTANTANEA_ID))
        .andExpect(jsonPath("$.balance").value(5500.00))
        .andExpect(jsonPath("$.income").value(2600.00))
        .andExpect(jsonPath("$.expenses").value(950.00))
        .andExpect(jsonPath("$.contribution").value(200.00))
        .andExpect(jsonPath("$.notes").value("Julio actualizado"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(InstantaneaMensualMO.class).hasLoadCount(1).hasUpdateCount(1)
        .verify();
  }

  @Test
  @Order(13)
  void eliminarInstantanea_returns204(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(delete("/instantaneas/" + INSTANTANEA_ID))
        .andExpect(status().isNoContent());

    StatisticsAssert.assertThat(statistics())
        .forEntity(InstantaneaMensualMO.class).hasLoadCount(1).hasDeleteCount(1)
        .verify();
  }

  @Test
  @Order(14)
  void eliminarInstantaneaCreadaPorUpsert_returns204(CapturedOutput output) throws Exception {
    statistics().clear();

    String upsertId = CUENTA_ID + "-2026-06";

    mockMvc.perform(delete("/instantaneas/" + upsertId))
        .andExpect(status().isNoContent());

    StatisticsAssert.assertThat(statistics())
        .forEntity(InstantaneaMensualMO.class).hasLoadCount(1).hasDeleteCount(1)
        .verify();
  }

  @Test
  @Order(15)
  void eliminarCuentaBase_returns204(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(delete("/cuentas/" + CUENTA_ID))
        .andExpect(status().isNoContent());

    StatisticsAssert.assertThat(statistics())
        .forEntity(CuentaMO.class).hasLoadCount(1).hasDeleteCount(1)
        .verify();
  }

  @Test
  @Order(16)
  void eliminarPlataformaBase_returns204(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(delete("/plataformas/" + PLATAFORMA_ID))
        .andExpect(status().isNoContent());

    StatisticsAssert.assertThat(statistics())
        .forEntity(PlataformaMO.class).hasLoadCount(1).hasDeleteCount(1)
        .verify();
  }*/
}
