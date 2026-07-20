package com.centimo.api.it;

import com.centimo.api.it.support.StatisticsAssert;
import com.centimo.api.database.models.FondoMyInvestorMO;
import com.centimo.api.database.repositories.FondoMyInvestorRepository;
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
class FondoMyInvestorControllerIT extends AbstractIntegrationIT {

  private static final String FONDO_ID = "fondo-myinvestor-it-1";

  @Autowired FondoMyInvestorRepository fondoRepository;

  @Test
  @Order(1)
  void crearFondo_returns201(CapturedOutput output) throws Exception {
    mockMvc.perform(post("/fondos-myinvestor")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "%s",
                  "code": "ES0110272015",
                  "name": "MyInvestor Amundi Index MSCI USA"
                }
                """.formatted(FONDO_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(FONDO_ID))
        .andExpect(jsonPath("$.code").value("ES0110272015"))
        .andExpect(jsonPath("$.name").value("MyInvestor Amundi Index MSCI USA"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(FondoMyInvestorMO.class).hasInsertCount(1)
        .verify();
  }

  @Test
  @Order(2)
  void buscarTodos_returnsFondos(CapturedOutput output) throws Exception {
    mockMvc.perform(get("/fondos-myinvestor"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(FONDO_ID))
        .andExpect(jsonPath("$[0].code").value("ES0110272015"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(FondoMyInvestorMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(3)
  void buscarPorId_returnsFondo(CapturedOutput output) throws Exception {
    mockMvc.perform(get("/fondos-myinvestor/" + FONDO_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(FONDO_ID))
        .andExpect(jsonPath("$.name").value("MyInvestor Amundi Index MSCI USA"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(FondoMyInvestorMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(4)
  void actualizarFondo_returns200(CapturedOutput output) throws Exception {
    mockMvc.perform(put("/fondos-myinvestor/" + FONDO_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "%s",
                  "code": "ES0110272015",
                  "name": "MyInvestor Amundi Index MSCI World"
                }
                """.formatted(FONDO_ID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("MyInvestor Amundi Index MSCI World"));

    StatisticsAssert.assertThat(statistics())
        .forEntity(FondoMyInvestorMO.class).hasLoadCount(1).hasUpdateCount(1)
        .verify();
  }

  @Test
  @Order(5)
  void eliminarFondo_returns204(CapturedOutput output) throws Exception {
    mockMvc.perform(delete("/fondos-myinvestor/" + FONDO_ID))
        .andExpect(status().isNoContent());

    StatisticsAssert.assertThat(statistics())
        .forEntity(FondoMyInvestorMO.class).hasLoadCount(1).hasDeleteCount(1)
        .verify();
  }
}
