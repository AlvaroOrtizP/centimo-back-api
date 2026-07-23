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
class B100IT extends AbstractIntegrationIT {

    /**
     * Test de integracion de la pantalla B100 (plataformas + cuentas + instantaneas):
     * 1. Comprobar tablas vacias
     * 2. Crear registro en B100 Save + instantanea mensual
     * 3. Crear registro en B100 Heal + instantanea mensual
     * 4. Comprobar tablas
     * 5. Editar ambos registros
     * 6. Comprobar tablas
     * 7. Eliminar registro
     * 8. Comprobar tablas
     */

    private static final String PLATAFORMA_ID = "b100-it";
    private static final String SAVE_CUENTA_ID = "b100-it-save";
    private static final String HEAL_CUENTA_ID = "b100-it-heal";

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
    void crearB100Save(CapturedOutput capturedOutput) throws Exception {
        jdbcTemplate.update(
            "INSERT INTO plataformas (id, nombre, tipo, color, icono, orden) VALUES (?, ?, ?, ?, ?, ?)",
            PLATAFORMA_ID, "B100 IT", "banco", "#6C3FD1", "smartphone", 1);

        jdbcTemplate.update(
            "INSERT INTO cuentas (id, plataforma_id, nombre, tipo, moneda, orden) VALUES (?, ?, ?, ?, ?, ?)",
            SAVE_CUENTA_ID, PLATAFORMA_ID, "Save", "ahorro", "EUR", 1);

        mockMvc.perform(post("/snapshots/upsert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "accountId": "%s",
                      "year": 2026,
                      "month": 7,
                      "balance": 1500.00,
                      "incomeDelta": 0,
                      "expenses": 200.00,
                      "contribution": 300.00
                    }
                    """.formatted(SAVE_CUENTA_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(SAVE_CUENTA_ID))
            .andExpect(jsonPath("$.balance").value(1500.00));
    }

    @Test
    @Order(3)
    void crearB100Heal(CapturedOutput capturedOutput) throws Exception {
        jdbcTemplate.update(
            "INSERT INTO cuentas (id, plataforma_id, nombre, tipo, moneda, orden) VALUES (?, ?, ?, ?, ?, ?)",
            HEAL_CUENTA_ID, PLATAFORMA_ID, "Health", "inversion", "EUR", 2);

        mockMvc.perform(post("/snapshots/upsert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "accountId": "%s",
                      "year": 2026,
                      "month": 7,
                      "balance": 800.00,
                      "incomeDelta": 0,
                      "expenses": 50.00,
                      "contribution": 100.00
                    }
                    """.formatted(HEAL_CUENTA_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(HEAL_CUENTA_ID))
            .andExpect(jsonPath("$.balance").value(800.00));
    }

    @Test
    @Order(4)
    void comprobarTablas(CapturedOutput capturedOutput) throws Exception {
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plataformas", Integer.class)).isOne();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cuentas", Integer.class)).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instantaneas_mensuales", Integer.class)).isEqualTo(2);

        String plataformaNombre = jdbcTemplate.queryForObject(
            "SELECT nombre FROM plataformas WHERE id = ?", String.class, PLATAFORMA_ID);
        assertThat(plataformaNombre).isEqualTo("B100 IT");

        String saveNombre = jdbcTemplate.queryForObject(
            "SELECT nombre FROM cuentas WHERE id = ?", String.class, SAVE_CUENTA_ID);
        assertThat(saveNombre).isEqualTo("Save");

        String healNombre = jdbcTemplate.queryForObject(
            "SELECT nombre FROM cuentas WHERE id = ?", String.class, HEAL_CUENTA_ID);
        assertThat(healNombre).isEqualTo("Health");

        Float saveBalance = jdbcTemplate.queryForObject(
            "SELECT saldo FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, SAVE_CUENTA_ID, 2026, 7);
        assertThat(saveBalance).isEqualTo(1500.00f);

        Float healBalance = jdbcTemplate.queryForObject(
            "SELECT saldo FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, HEAL_CUENTA_ID, 2026, 7);
        assertThat(healBalance).isEqualTo(800.00f);

        Float saveGastos = jdbcTemplate.queryForObject(
            "SELECT gastos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, SAVE_CUENTA_ID, 2026, 7);
        assertThat(saveGastos).isEqualTo(200.00f);

        Float saveAportacion = jdbcTemplate.queryForObject(
            "SELECT aportacion FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, SAVE_CUENTA_ID, 2026, 7);
        assertThat(saveAportacion).isEqualTo(300.00f);

        Float healGastos = jdbcTemplate.queryForObject(
            "SELECT gastos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, HEAL_CUENTA_ID, 2026, 7);
        assertThat(healGastos).isEqualTo(50.00f);

        Float healAportacion = jdbcTemplate.queryForObject(
            "SELECT aportacion FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, HEAL_CUENTA_ID, 2026, 7);
        assertThat(healAportacion).isEqualTo(100.00f);
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
                      "balance": 1800.00,
                      "incomeDelta": 0,
                      "expenses": 250.00,
                      "contribution": 350.00
                    }
                    """.formatted(SAVE_CUENTA_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance").value(1800.00));

        mockMvc.perform(post("/snapshots/upsert")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "accountId": "%s",
                      "year": 2026,
                      "month": 7,
                      "balance": 950.00,
                      "incomeDelta": 0,
                      "expenses": 75.00,
                      "contribution": 150.00
                    }
                    """.formatted(HEAL_CUENTA_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance").value(950.00));
    }

    @Test
    @Order(6)
    void comprobarTablasDespuesEdicion(CapturedOutput capturedOutput) throws Exception {
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plataformas", Integer.class)).isOne();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cuentas", Integer.class)).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instantaneas_mensuales", Integer.class)).isEqualTo(2);

        Float saveBalance = jdbcTemplate.queryForObject(
            "SELECT saldo FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, SAVE_CUENTA_ID, 2026, 7);
        assertThat(saveBalance).isEqualTo(1800.00f);

        Float saveIngresos = jdbcTemplate.queryForObject(
            "SELECT ingresos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, SAVE_CUENTA_ID, 2026, 7);
        assertThat(saveIngresos).isEqualTo(0f);

        Float healBalance = jdbcTemplate.queryForObject(
            "SELECT saldo FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, HEAL_CUENTA_ID, 2026, 7);
        assertThat(healBalance).isEqualTo(950.00f);

        Float healIngresos = jdbcTemplate.queryForObject(
            "SELECT ingresos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, HEAL_CUENTA_ID, 2026, 7);
        assertThat(healIngresos).isEqualTo(0f);

        Float saveGastos = jdbcTemplate.queryForObject(
            "SELECT gastos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, SAVE_CUENTA_ID, 2026, 7);
        assertThat(saveGastos).isEqualTo(250.00f);

        Float saveAportacion = jdbcTemplate.queryForObject(
            "SELECT aportacion FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, SAVE_CUENTA_ID, 2026, 7);
        assertThat(saveAportacion).isEqualTo(350.00f);

        Float healGastos = jdbcTemplate.queryForObject(
            "SELECT gastos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, HEAL_CUENTA_ID, 2026, 7);
        assertThat(healGastos).isEqualTo(75.00f);

        Float healAportacion = jdbcTemplate.queryForObject(
            "SELECT aportacion FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, HEAL_CUENTA_ID, 2026, 7);
        assertThat(healAportacion).isEqualTo(150.00f);
    }

    @Test
    @Order(7)
    void eliminarRegistro(CapturedOutput capturedOutput) throws Exception {
        Integer instantaneasBefore = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM instantaneas_mensuales WHERE cuenta_id = ?",
            Integer.class, HEAL_CUENTA_ID);
        assertThat(instantaneasBefore).isOne();

        jdbcTemplate.update("DELETE FROM instantaneas_mensuales WHERE cuenta_id = ?", HEAL_CUENTA_ID);
        jdbcTemplate.update("DELETE FROM cuentas WHERE id = ?", HEAL_CUENTA_ID);
    }

    @Test
    @Order(8)
    void comprobarTablasDespuesEliminacion(CapturedOutput capturedOutput) throws Exception {
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plataformas", Integer.class)).isOne();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cuentas", Integer.class)).isOne();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instantaneas_mensuales", Integer.class)).isOne();

        String saveNombre = jdbcTemplate.queryForObject(
            "SELECT nombre FROM cuentas WHERE id = ?", String.class, SAVE_CUENTA_ID);
        assertThat(saveNombre).isEqualTo("Save");

        Float saveBalance = jdbcTemplate.queryForObject(
            "SELECT saldo FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, SAVE_CUENTA_ID, 2026, 7);
        assertThat(saveBalance).isEqualTo(1800.00f);

        Float saveIngresos = jdbcTemplate.queryForObject(
            "SELECT ingresos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, SAVE_CUENTA_ID, 2026, 7);
        assertThat(saveIngresos).isEqualTo(0f);

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM gastos", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM posiciones_inversion", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM operaciones_inversion", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM inversiones_crowdlending", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM nomina", Integer.class)).isZero();
    }
}
