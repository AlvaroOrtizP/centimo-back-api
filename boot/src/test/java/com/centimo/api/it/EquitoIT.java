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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EquitoIT extends AbstractIntegrationIT {

    /**
     * Test de integracion de la pantalla Equito (inversiones crowdlending + balance mensual):
     * 1. Comprobar tablas vacias
     * 2. Crear plataforma + cuenta + inversion crowdlending + instantanea mensual
     * 3. Listar por API y comprobar tablas
     * 4. Editar balance (upsert) y comprobar actualizacion
     * 5. Eliminar inversion y comprobar tablas
     */

    private static final String PLATAFORMA_ID = "equito-it";
    private static final String CUENTA_ID = "equito-it-main";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Order(1)
    void tablasVacias(CapturedOutput capturedOutput) throws Exception {
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plataformas", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cuentas", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM inversiones_crowdlending", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instantaneas_mensuales", Integer.class)).isZero();
    }

    @Test
    @Order(2)
    void crearEquito(CapturedOutput capturedOutput) throws Exception {
        jdbcTemplate.update(
            "INSERT INTO plataformas (id, nombre, tipo, color, icono, orden) VALUES (?, ?, ?, ?, ?, ?)",
            PLATAFORMA_ID, "Equito IT", "crowdlending", "#FF6B35", "home", 1);

        jdbcTemplate.update(
            "INSERT INTO cuentas (id, plataforma_id, nombre, tipo, moneda, orden) VALUES (?, ?, ?, ?, ?, ?)",
            CUENTA_ID, PLATAFORMA_ID, "Principal", "inversion", "EUR", 1);

        mockMvc.perform(post("/crowdlending")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "platformId": "%s",
                      "projectName": "Préstamo personal IT",
                      "investedAmount": 2000.00,
                      "interestRate": 9.5,
                      "termMonths": 0,
                      "startDate": "2026-07-01",
                      "endDate": null,
                      "monthlyReturn": 15.83,
                      "totalReturned": 0,
                      "status": "active"
                    }
                    """.formatted(PLATAFORMA_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.platformId").value(PLATAFORMA_ID))
            .andExpect(jsonPath("$.projectName").value("Préstamo personal IT"))
            .andExpect(jsonPath("$.endDate").isEmpty());

        mockMvc.perform(post("/snapshots/upsert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "accountId": "%s",
                      "year": 2026,
                      "month": 7,
                      "balance": 4000.00,
                      "incomeDelta": 25,
                      "expenses": 50,
                      "contribution": 200
                    }
                    """.formatted(CUENTA_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(CUENTA_ID))
            .andExpect(jsonPath("$.balance").value(4000.00))
            .andExpect(jsonPath("$.income").value(25.0))
            .andExpect(jsonPath("$.expenses").value(50.0))
            .andExpect(jsonPath("$.contribution").value(200.0));
    }

    @Test
    @Order(3)
    void listarYComprobarTablas(CapturedOutput capturedOutput) throws Exception {
        mockMvc.perform(get("/crowdlending").param("platformId", PLATAFORMA_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].projectName").value("Préstamo personal IT"))
            .andExpect(jsonPath("$[0].startDate").value("2026-07-01"));

        mockMvc.perform(get("/snapshots"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].accountId").value(CUENTA_ID))
            .andExpect(jsonPath("$[0].balance").value(4000.00))
            .andExpect(jsonPath("$[0].income").value(25.0))
            .andExpect(jsonPath("$[0].expenses").value(50.0))
            .andExpect(jsonPath("$[0].contribution").value(200.0));

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM inversiones_crowdlending", Integer.class)).isOne();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instantaneas_mensuales", Integer.class)).isOne();

        String fechaInicio = jdbcTemplate.queryForObject(
            "SELECT fecha_inicio FROM inversiones_crowdlending WHERE plataforma_id = ?",
            String.class, PLATAFORMA_ID);
        assertThat(fechaInicio).isEqualTo("2026-07-01");

        String estado = jdbcTemplate.queryForObject(
            "SELECT estado FROM inversiones_crowdlending WHERE plataforma_id = ?",
            String.class, PLATAFORMA_ID);
        assertThat(estado).isEqualTo("active");

        Float saldo = jdbcTemplate.queryForObject(
            "SELECT saldo FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(saldo).isEqualTo(4000.00f);

        Float ingresos = jdbcTemplate.queryForObject(
            "SELECT ingresos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(ingresos).isEqualTo(25.00f);

        Float gastos = jdbcTemplate.queryForObject(
            "SELECT gastos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(gastos).isEqualTo(50.00f);

        Float aportacion = jdbcTemplate.queryForObject(
            "SELECT aportacion FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(aportacion).isEqualTo(200.00f);
    }

    @Test
    @Order(4)
    void editarBalance(CapturedOutput capturedOutput) throws Exception {
        mockMvc.perform(post("/snapshots/upsert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "accountId": "%s",
                      "year": 2026,
                      "month": 7,
                      "balance": 4500.00,
                      "incomeDelta": 30,
                      "expenses": 20,
                      "contribution": 150
                    }
                    """.formatted(CUENTA_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance").value(4500.00))
            .andExpect(jsonPath("$.contribution").value(150.0));

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instantaneas_mensuales", Integer.class)).isOne();

        Float saldo = jdbcTemplate.queryForObject(
            "SELECT saldo FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(saldo).isEqualTo(4500.00f);

        Float ingresos = jdbcTemplate.queryForObject(
            "SELECT ingresos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(ingresos).isEqualTo(55.00f);

        Float gastos = jdbcTemplate.queryForObject(
            "SELECT gastos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(gastos).isEqualTo(20.00f);

        Float aportacion = jdbcTemplate.queryForObject(
            "SELECT aportacion FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(aportacion).isEqualTo(150.00f);
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
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instantaneas_mensuales", Integer.class)).isOne();
    }
}
