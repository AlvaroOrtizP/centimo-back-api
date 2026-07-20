package com.centimo.api.it;

import com.centimo.api.it.support.StatisticsAssert;
import com.centimo.api.database.models.CuentaMO;
import com.centimo.api.database.models.PlataformaMO;
import com.centimo.api.database.repositories.CuentaRepository;
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
class CuentaControllerIT extends AbstractIntegrationIT {

  private static final String PLATAFORMA_ID = "myinvestor";
  private static final String CUENTA_ID = "cuenta-ahorro-1";

  @Autowired
  PlataformaRepository plataformaRepository;

  @Autowired
  CuentaRepository cuentaRepository;

  @Test
  @Order(1)
  void crearPlataformaBase(CapturedOutput output) throws Exception {
    mockMvc.perform(post("/plataformas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id":"%s","name":"MyInvestor","type":"inversion","color":"#FF5722","icon":"trending-up","order":1}
                """.formatted(PLATAFORMA_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(PLATAFORMA_ID));

    StatisticsAssert.assertThat(statistics())
        .forEntity(PlataformaMO.class).hasInsertCount(1)
        .verify();
  }

  @Test
  @Order(2)
  void crearCuenta_returns201(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(post("/cuentas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id":"%s","platformId":"%s","name":"Ahorro","type":"ahorro","currency":"EUR","order":1}
                """.formatted(CUENTA_ID, PLATAFORMA_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(CUENTA_ID))
        .andExpect(jsonPath("$.platformId").value(PLATAFORMA_ID))
        .andExpect(jsonPath("$.name").value("Ahorro"))
        .andExpect(jsonPath("$.type").value("ahorro"))
        .andExpect(jsonPath("$.currency").value("EUR"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(CuentaMO.class).hasInsertCount(1)
        .verify();
  }

  @Test
  @Order(3)
  void buscarPorId_returnsCuenta(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(get("/cuentas/" + CUENTA_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(CUENTA_ID))
        .andExpect(jsonPath("$.platformId").value(PLATAFORMA_ID))
        .andExpect(jsonPath("$.name").value("Ahorro"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(CuentaMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(4)
  void buscarTodasConFiltroPlataforma(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(get("/cuentas").param("plataformaId", PLATAFORMA_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].platformId").value(PLATAFORMA_ID));

    StatisticsAssert.assertThat(statistics())
        .forEntity(CuentaMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(5)
  void actualizarCuenta_returns200(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(put("/cuentas/" + CUENTA_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"platformId":"%s","name":"Ahorro Plus","type":"inversion","currency":"EUR","order":2}
                """.formatted(PLATAFORMA_ID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(CUENTA_ID))
        .andExpect(jsonPath("$.name").value("Ahorro Plus"))
        .andExpect(jsonPath("$.type").value("inversion"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(CuentaMO.class).hasLoadCount(1).hasUpdateCount(1)
        .verify();
  }

  @Test
  @Order(6)
  void eliminarCuenta_returns204(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(delete("/cuentas/" + CUENTA_ID))
        .andExpect(status().isNoContent());

    StatisticsAssert.assertThat(statistics())
        .forEntity(CuentaMO.class).hasLoadCount(1).hasDeleteCount(1)
        .verify();
  }

  @Test
  @Order(7)
  void eliminarPlataformaBase_returns204(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(delete("/plataformas/" + PLATAFORMA_ID))
        .andExpect(status().isNoContent());

    StatisticsAssert.assertThat(statistics())
        .forEntity(PlataformaMO.class).hasLoadCount(1).hasDeleteCount(1)
        .verify();
  }
}
