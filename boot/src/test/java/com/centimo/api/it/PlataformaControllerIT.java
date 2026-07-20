package com.centimo.api.it;

import com.centimo.api.it.support.StatisticsAssert;
import com.centimo.api.database.models.PlataformaMO;
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
class PlataformaControllerIT extends AbstractIntegrationIT {

  private static final String PLATAFORMA_ID = "bbva";

  @Autowired
  PlataformaRepository plataformaRepository;

  @Test
  @Order(1)
  void crearPlataforma_returns201(CapturedOutput output) throws Exception {
    mockMvc.perform(post("/plataformas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id":"bbva","name":"BBVA","type":"banco","color":"#004481","icon":"building","order":1}
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(PLATAFORMA_ID))
        .andExpect(jsonPath("$.name").value("BBVA"))
        .andExpect(jsonPath("$.type").value("banco"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(PlataformaMO.class).hasInsertCount(1)
        .verify();
  }

  @Test
  @Order(2)
  void buscarTodas_returnsSeededPlataformas(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(get("/plataformas"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(PLATAFORMA_ID))
        .andExpect(jsonPath("$[0].name").value("BBVA"))
        .andExpect(jsonPath("$[0].type").value("banco"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(PlataformaMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(3)
  void actualizarPlataforma_returns200(CapturedOutput output) throws Exception {
    statistics().clear();

    mockMvc.perform(put("/plataformas/" + PLATAFORMA_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name":"BBVA España","type":"banco","color":"#FF0000","icon":"building","order":2}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(PLATAFORMA_ID))
        .andExpect(jsonPath("$.name").value("BBVA España"))
        .andExpect(jsonPath("$.color").value("#FF0000"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(PlataformaMO.class).hasLoadCount(1).hasUpdateCount(1)
        .verify();
  }
}
