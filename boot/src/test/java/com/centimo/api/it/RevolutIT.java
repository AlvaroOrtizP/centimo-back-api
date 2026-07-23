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
class RevolutIT extends AbstractIntegrationIT {

    /**
     * Test de integracion de la pantalla Revolut (plataforma + cuenta + instantaneas):
     * 1. Comprobar tablas vacias
     * 2. Crear registro en Revolut + instantanea mensual
     * 3. Comprobar tablas
     * 4. Editar registro
     * 5. Comprobar tablas
     */

    private static final String PLATAFORMA_ID = "revolut-it";
    private static final String CUENTA_ID = "revolut-it-main";

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
    void crearRevolut(CapturedOutput capturedOutput) throws Exception {
        jdbcTemplate.update(
            "INSERT INTO plataformas (id, nombre, tipo, color, icono, orden) VALUES (?, ?, ?, ?, ?, ?)",
            PLATAFORMA_ID, "Revolut IT", "banco", "#EB008B", "smartphone", 1);

        jdbcTemplate.update(
            "INSERT INTO cuentas (id, plataforma_id, nombre, tipo, moneda, orden) VALUES (?, ?, ?, ?, ?, ?)",
            CUENTA_ID, PLATAFORMA_ID, "Principal", "corriente", "EUR", 1);

        mockMvc.perform(post("/snapshots/upsert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "accountId": "%s",
                      "year": 2026,
                      "month": 7,
                      "balance": 2100.00,
                      "incomeDelta": 1500.00,
                      "expenses": 0
                    }
                    """.formatted(CUENTA_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(CUENTA_ID))
            .andExpect(jsonPath("$.balance").value(2100.00));
    }

    @Test
    @Order(3)
    void comprobarTablas(CapturedOutput capturedOutput) throws Exception {
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plataformas", Integer.class)).isOne();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cuentas", Integer.class)).isOne();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instantaneas_mensuales", Integer.class)).isOne();

        String plataformaNombre = jdbcTemplate.queryForObject(
            "SELECT nombre FROM plataformas WHERE id = ?", String.class, PLATAFORMA_ID);
        assertThat(plataformaNombre).isEqualTo("Revolut IT");

        String cuentaNombre = jdbcTemplate.queryForObject(
            "SELECT nombre FROM cuentas WHERE id = ?", String.class, CUENTA_ID);
        assertThat(cuentaNombre).isEqualTo("Principal");

        Float saldo = jdbcTemplate.queryForObject(
            "SELECT saldo FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(saldo).isEqualTo(2100.00f);

        Float ingresos = jdbcTemplate.queryForObject(
            "SELECT ingresos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(ingresos).isEqualTo(1500.00f);
    }

    @Test
    @Order(4)
    void editarRegistro(CapturedOutput capturedOutput) throws Exception {
        mockMvc.perform(post("/snapshots/upsert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "accountId": "%s",
                      "year": 2026,
                      "month": 7,
                      "balance": 2400.00,
                      "incomeDelta": 300.00,
                      "expenses": 0
                    }
                    """.formatted(CUENTA_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance").value(2400.00));
    }

    @Test
    @Order(5)
    void comprobarTablasDespuesEdicion(CapturedOutput capturedOutput) throws Exception {
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plataformas", Integer.class)).isOne();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cuentas", Integer.class)).isOne();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instantaneas_mensuales", Integer.class)).isOne();

        Float saldo = jdbcTemplate.queryForObject(
            "SELECT saldo FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(saldo).isEqualTo(2400.00f);

        Float ingresos = jdbcTemplate.queryForObject(
            "SELECT ingresos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(ingresos).isEqualTo(1800.00f);

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM gastos", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM posiciones_inversion", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM operaciones_inversion", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM inversiones_crowdlending", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM nomina", Integer.class)).isZero();
    }
}
