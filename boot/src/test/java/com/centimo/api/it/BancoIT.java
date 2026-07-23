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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BancoIT extends AbstractIntegrationIT {

    /**
     * Test de integracion de la pantalla bancos (plataformas + cuentas + instantaneas):
     * 1. Comprobar tablas vacias
     * 2. Crear registro en Caixa + instantanea mensual
     * 3. Crear registro en BBVA + instantanea mensual
     * 4. Comprobar tablas
     * 5. Editar ambos registros
     * 6. Comprobar tablas
     */

    private static final String CAIXA_PLATAFORMA_ID = "caixabank-it";
    private static final String CAIXA_CUENTA_ID = "caixabank-it-principal";
    private static final String BBVA_PLATAFORMA_ID = "bbva-it";
    private static final String BBVA_CUENTA_ID = "bbva-it-nomina";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Order(1)
    void tablasVacias(CapturedOutput capturedOutput) throws Exception {
        Integer plataformasCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plataformas", Integer.class);
        Integer cuentasCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cuentas", Integer.class);

        assertThat(plataformasCount).isZero();
        assertThat(cuentasCount).isZero();
    }

    @Test
    @Order(2)
    void crearCaixa(CapturedOutput capturedOutput) throws Exception {
        jdbcTemplate.update(
            "INSERT INTO plataformas (id, nombre, tipo, color, icono, orden) VALUES (?, ?, ?, ?, ?, ?)",
            CAIXA_PLATAFORMA_ID, "CaixaBank IT", "banco", "#FF5722", "building", 1);

        jdbcTemplate.update(
            "INSERT INTO cuentas (id, plataforma_id, nombre, tipo, moneda, orden) VALUES (?, ?, ?, ?, ?, ?)",
            CAIXA_CUENTA_ID, CAIXA_PLATAFORMA_ID, "Principal", "corriente", "EUR", 1);

        // crear instancia_mensual
        mockMvc.perform(post("/snapshots/upsert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "accountId": "%s",
                      "year": 2026,
                      "month": 7,
                      "balance": 3200.00,
                      "incomeDelta": 1800.00,
                      "expenses": 0
                    }
                    """.formatted(CAIXA_CUENTA_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(CAIXA_CUENTA_ID))
            .andExpect(jsonPath("$.balance").value(3200.00));
    }

    @Test
    @Order(3)
    void crearBBVA(CapturedOutput capturedOutput) throws Exception {
        jdbcTemplate.update(
            "INSERT INTO plataformas (id, nombre, tipo, color, icono, orden) VALUES (?, ?, ?, ?, ?, ?)",
            BBVA_PLATAFORMA_ID, "BBVA IT", "banco", "#004481", "building", 2);

        jdbcTemplate.update(
            "INSERT INTO cuentas (id, plataforma_id, nombre, tipo, moneda, orden) VALUES (?, ?, ?, ?, ?, ?)",
            BBVA_CUENTA_ID, BBVA_PLATAFORMA_ID, "Nomina IT", "corriente", "EUR", 1);

        mockMvc.perform(post("/snapshots/upsert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "accountId": "%s",
                      "year": 2026,
                      "month": 7,
                      "balance": 5000.00,
                      "incomeDelta": 2200.00,
                      "expenses": 0
                    }
                    """.formatted(BBVA_CUENTA_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(BBVA_CUENTA_ID))
            .andExpect(jsonPath("$.balance").value(5000.00));
    }

    @Test
    @Order(4)
    void comprobarTablas(CapturedOutput capturedOutput) throws Exception {
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plataformas", Integer.class)).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cuentas", Integer.class)).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instantaneas_mensuales", Integer.class)).isEqualTo(2);

        String caixaNombre = jdbcTemplate.queryForObject(
            "SELECT nombre FROM plataformas WHERE id = ?", String.class, CAIXA_PLATAFORMA_ID);
        assertThat(caixaNombre).isEqualTo("CaixaBank IT");

        String caixaCuentaNombre = jdbcTemplate.queryForObject(
            "SELECT nombre FROM cuentas WHERE id = ?", String.class, CAIXA_CUENTA_ID);
        assertThat(caixaCuentaNombre).isEqualTo("Principal");

        String bbvaNombre = jdbcTemplate.queryForObject(
            "SELECT nombre FROM plataformas WHERE id = ?", String.class, BBVA_PLATAFORMA_ID);
        assertThat(bbvaNombre).isEqualTo("BBVA IT");

        String bbvaCuentaNombre = jdbcTemplate.queryForObject(
            "SELECT nombre FROM cuentas WHERE id = ?", String.class, BBVA_CUENTA_ID);
        assertThat(bbvaCuentaNombre).isEqualTo("Nomina IT");

        Float caixaBalance = jdbcTemplate.queryForObject(
            "SELECT saldo FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CAIXA_CUENTA_ID, 2026, 7);
        assertThat(caixaBalance).isEqualTo(3200.00f);

        Float bbvaBalance = jdbcTemplate.queryForObject(
            "SELECT saldo FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, BBVA_CUENTA_ID, 2026, 7);
        assertThat(bbvaBalance).isEqualTo(5000.00f);
    }

    @Test
    @Order(5)
    void editarAmbosRegistros(CapturedOutput capturedOutput) throws Exception {
        mockMvc.perform(post("/snapshots/upsert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "accountId": "%s",
                      "year": 2026,
                      "month": 7,
                      "balance": 3500.00,
                      "incomeDelta": 0,
                      "expenses": 100.00
                    }
                    """.formatted(CAIXA_CUENTA_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance").value(3500.00));

        mockMvc.perform(post("/snapshots/upsert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "accountId": "%s",
                      "year": 2026,
                      "month": 7,
                      "balance": 4800.00,
                      "incomeDelta": 0,
                      "expenses": 200.00
                    }
                    """.formatted(BBVA_CUENTA_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance").value(4800.00));
    }

    @Test
    @Order(6)
    void comprobarTablasDespuesEdicion(CapturedOutput capturedOutput) throws Exception {
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plataformas", Integer.class)).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cuentas", Integer.class)).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instantaneas_mensuales", Integer.class)).isEqualTo(2);

        Float caixaBalance = jdbcTemplate.queryForObject(
            "SELECT saldo FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CAIXA_CUENTA_ID, 2026, 7);
        assertThat(caixaBalance).isEqualTo(3500.00f);

        Float caixaGastos = jdbcTemplate.queryForObject(
            "SELECT gastos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CAIXA_CUENTA_ID, 2026, 7);
        assertThat(caixaGastos).isEqualTo(100.00f);

        Float bbvaBalance = jdbcTemplate.queryForObject(
            "SELECT saldo FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, BBVA_CUENTA_ID, 2026, 7);
        assertThat(bbvaBalance).isEqualTo(4800.00f);

        Float bbvaGastos = jdbcTemplate.queryForObject(
            "SELECT gastos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, BBVA_CUENTA_ID, 2026, 7);
        assertThat(bbvaGastos).isEqualTo(200.00f);

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM gastos", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM posiciones_inversion", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM operaciones_inversion", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM inversiones_crowdlending", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM nomina", Integer.class)).isZero();
    }
}
