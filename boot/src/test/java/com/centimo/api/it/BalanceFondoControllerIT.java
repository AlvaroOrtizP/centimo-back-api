package com.centimo.api.it;

import com.centimo.api.it.support.StatisticsAssert;
import com.centimo.api.database.models.BalanceFondoMO;
import com.centimo.api.database.repositories.BalanceFondoRepository;
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
class BalanceFondoControllerIT extends AbstractIntegrationIT {

  private static final String FONDO_ID = "fondo-balance-it-1";
  private static final String BALANCE_ID = "balance-fondo-it-1";

  @Autowired FondoMyInvestorRepository fondoRepository;
  @Autowired BalanceFondoRepository balanceRepository;

  @Test
  @Order(1)
  void setupBaseData() throws Exception {
    mockMvc.perform(post("/fondos-myinvestor")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "%s",
                  "code": "ES0110272015",
                  "name": "MyInvestor Amundi Index MSCI USA"
                }
                """.formatted(FONDO_ID)))
        .andExpect(status().isCreated());
  }

  @Test
  @Order(2)
  void crearBalance_returns201(CapturedOutput output) throws Exception {
    mockMvc.perform(post("/balances-fondo")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "%s",
                  "fundId": "%s",
                  "year": 2026,
                  "month": 7,
                  "balance": 12500.00
                }
                """.formatted(BALANCE_ID, FONDO_ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(BALANCE_ID))
        .andExpect(jsonPath("$.fundId").value(FONDO_ID))
        .andExpect(jsonPath("$.year").value(2026))
        .andExpect(jsonPath("$.month").value(7))
        .andExpect(jsonPath("$.balance").value(12500.00));

    StatisticsAssert.assertThat(statistics())
        .forEntity(BalanceFondoMO.class).hasInsertCount(1)
        .verify();
  }

  @Test
  @Order(3)
  void buscarTodos_returnsBalances(CapturedOutput output) throws Exception {
    mockMvc.perform(get("/balances-fondo")
            .param("anio", "2026")
            .param("mes", "7"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(BALANCE_ID))
        .andExpect(jsonPath("$[0].balance").value(12500.00));

    StatisticsAssert.assertThat(statistics())
        .forEntity(BalanceFondoMO.class).hasLoadCount(1)
        .verify();
  }

  @Test
  @Order(4)
  void actualizarBalance_returns200(CapturedOutput output) throws Exception {
    mockMvc.perform(put("/balances-fondo/" + BALANCE_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "id": "%s",
                  "fundId": "%s",
                  "year": 2026,
                  "month": 7,
                  "balance": 13000.00
                }
                """.formatted(BALANCE_ID, FONDO_ID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance").value(13000.00));

    StatisticsAssert.assertThat(statistics())
        .forEntity(BalanceFondoMO.class).hasLoadCount(1).hasUpdateCount(1)
        .verify();
  }

  @Test
  @Order(5)
  void eliminarBalance_returns204(CapturedOutput output) throws Exception {
    mockMvc.perform(delete("/balances-fondo/" + BALANCE_ID))
        .andExpect(status().isNoContent());

    StatisticsAssert.assertThat(statistics())
        .forEntity(BalanceFondoMO.class).hasLoadCount(1).hasDeleteCount(1)
        .verify();
  }

  @Test
  @Order(6)
  void cleanup() throws Exception {
    mockMvc.perform(delete("/fondos-myinvestor/" + FONDO_ID)).andExpect(status().isNoContent());
  }
}
