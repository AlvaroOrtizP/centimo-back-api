package com.centimo.api.it;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CrowdlendingIT extends AbstractIntegrationIT {

    private static final String PLATAFORMA_ID = "urbanitae-it";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Order(1)
    void tablasVacias(CapturedOutput capturedOutput) throws Exception {
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plataformas", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM inversiones_crowdlending", Integer.class)).isZero();
    }

    @Test
    @Order(2)
    void crearInversion(CapturedOutput capturedOutput) throws Exception {
        jdbcTemplate.update(
            "INSERT INTO plataformas (id, nombre, tipo, color, icono, orden) VALUES (?, ?, ?, ?, ?, ?)",
            PLATAFORMA_ID, "Urbanitae IT", "crowdlending", "#E63946", "building", 1);

        mockMvc.perform(post("/crowdlending")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "platformId": "%s",
                      "projectName": "Vivienda Madrid",
                      "investedAmount": 1000.00,
                      "interestRate": 8.5,
                      "termMonths": 12,
                      "startDate": "2026-07-01",
                      "monthlyReturn": 7.08,
                      "totalReturned": 0,
                      "status": "active"
                    }
                    """.formatted(PLATAFORMA_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.platformId").value(PLATAFORMA_ID))
            .andExpect(jsonPath("$.projectName").value("Vivienda Madrid"))
            .andExpect(jsonPath("$.investedAmount").value(1000.00))
            .andExpect(jsonPath("$.interestRate").value(8.5))
            .andExpect(jsonPath("$.termMonths").value(12))
            .andExpect(jsonPath("$.status").value("active"));
    }

    @Test
    @Order(3)
    void listarPorPlataforma(CapturedOutput capturedOutput) throws Exception {
        mockMvc.perform(get("/crowdlending").param("platformId", PLATAFORMA_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].projectName").value("Vivienda Madrid"))
            .andExpect(jsonPath("$[0].totalReturned").value(0));
    }

    @Test
    @Order(4)
    void comprobarTablas(CapturedOutput capturedOutput) throws Exception {
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM inversiones_crowdlending", Integer.class)).isOne();

        String nombreProyecto = jdbcTemplate.queryForObject(
            "SELECT nombre_proyecto FROM inversiones_crowdlending WHERE plataforma_id = ?",
            String.class, PLATAFORMA_ID);
        assertThat(nombreProyecto).isEqualTo("Vivienda Madrid");

        Float cantidad = jdbcTemplate.queryForObject(
            "SELECT cantidad_invertida FROM inversiones_crowdlending WHERE plataforma_id = ?",
            Float.class, PLATAFORMA_ID);
        assertThat(cantidad).isEqualTo(1000.00f);

        String estado = jdbcTemplate.queryForObject(
            "SELECT estado FROM inversiones_crowdlending WHERE plataforma_id = ?",
            String.class, PLATAFORMA_ID);
        assertThat(estado).isEqualTo("active");
    }

    @Test
    @Order(5)
    void eliminarInversion(CapturedOutput capturedOutput) throws Exception {
        String id = jdbcTemplate.queryForObject(
            "SELECT id FROM inversiones_crowdlending WHERE plataforma_id = ?",
            String.class, PLATAFORMA_ID);

        mockMvc.perform(delete("/crowdlending/{id}", id))
            .andExpect(status().isNoContent());

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM inversiones_crowdlending", Integer.class)).isZero();
    }

    @Test
    @Order(6)
    void validarCamposObligatorios(CapturedOutput capturedOutput) throws Exception {
        mockMvc.perform(post("/crowdlending")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "platformId": "%s",
                      "projectName": "Sin fecha",
                      "investedAmount": 100,
                      "interestRate": 5,
                      "termMonths": 12,
                      "startDate": null,
                      "monthlyReturn": 0.42,
                      "totalReturned": 0,
                      "status": "active"
                    }
                    """.formatted(PLATAFORMA_ID)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("startDate")))
            .andExpect(jsonPath("$.errors[0].field").value("startDate"))
            .andExpect(jsonPath("$.errors[0].message").isNotEmpty());
    }
}
