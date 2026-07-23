package com.centimo.api.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
class GastoIT extends AbstractIntegrationIT {

    /**
     * En este test de integracion probamos todas las funcionaldiades de la pantalla gastos
     *
     * Comprobar que no existen datos
     *
     * Crear cuenta e instantanea para ese anio/mes
     *
     * Listar los gastos para ese registro: da 0
     *
     * Crear varios gastos, comprobar que se suman sus valores en la instantanea de ese mes para gastos
     *
     * Listar los gastos para ese registro: da 2
     *
     * Editar registros
     */

    private static final String PLATAFORMA_ID = "bbva-it";
    private static final String CUENTA_ID = "bbva-it-nomina";
    private static final String INSTANTANEA_ID = CUENTA_ID + "-2026-7";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void gastoFlow(CapturedOutput capturedOutput) throws Exception {
        // Verificar que no existen datos en gastos ni instantaneas
        Integer gastosCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM gastos", Integer.class);
        Integer instantaneasCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instantaneas_mensuales", Integer.class);

        assertThat(gastosCount).isZero();
        assertThat(instantaneasCount).isZero();

        // Crear plataforma y cuenta directamente (no hay endpoints REST aún)
        jdbcTemplate.update(
            "INSERT INTO plataformas (id, nombre, tipo, color, icono, orden) VALUES (?, ?, ?, ?, ?, ?)",
            PLATAFORMA_ID, "BBVA IT", "banco", "#004481", "building", 1);

        jdbcTemplate.update(
            "INSERT INTO cuentas (id, plataforma_id, nombre, tipo, moneda, orden) VALUES (?, ?, ?, ?, ?, ?)",
            CUENTA_ID, PLATAFORMA_ID, "Nomina IT", "corriente", "EUR", 1);

        // Llamada a upsertSnapshot
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
                    """.formatted(CUENTA_ID)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(CUENTA_ID))
            .andExpect(jsonPath("$.year").value(2026))
            .andExpect(jsonPath("$.month").value(7))
            .andExpect(jsonPath("$.expenses").value(0));

        // Comprobar que existe un registro nuevo y que su gasto es 0
        Integer instantaneaCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Integer.class, CUENTA_ID, 2026, 7);
        assertThat(instantaneaCount).isOne();

        Float gastosInstantanea = jdbcTemplate.queryForObject(
            "SELECT gastos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(gastosInstantanea).isEqualTo(0f);

        // Listar gastos de la instantánea (debe devolver vacío)
        mockMvc.perform(get("/expenses").param("snapshotId", INSTANTANEA_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Llamada a createExpense: gasto de 20 para el día 2
        mockMvc.perform(post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "snapshotId": "%s",
                      "category": "comida",
                      "amount": 20.0,
                      "date": "2026-07-02"
                    }
                    """.formatted(INSTANTANEA_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.amount").value(20.0));

        // Llamada a createExpense: gasto de 5 para el día 13
        mockMvc.perform(post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "snapshotId": "%s",
                      "category": "ocio",
                      "amount": 5.0,
                      "date": "2026-07-13"
                    }
                    """.formatted(INSTANTANEA_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.amount").value(5.0));

        // Comprobar que la instantánea tiene gastos totales de 25
        Float gastosTotal = jdbcTemplate.queryForObject(
            "SELECT gastos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(gastosTotal).isEqualTo(25f);

        // Listar gastos de la instantánea (debe devolver 2 registros)
        mockMvc.perform(get("/expenses").param("snapshotId", INSTANTANEA_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2));

        // Comprobar que hay 2 registros en gastos y 1 en instantanea
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM gastos", Integer.class)).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instantaneas_mensuales", Integer.class)).isOne();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plataformas", Integer.class)).isOne();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cuentas", Integer.class)).isOne();

        // Comprobar que el resto de tablas siguen vacías
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM posiciones_inversion", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM operaciones_inversion", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM inversiones_crowdlending", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM nomina", Integer.class)).isZero();
    }

    //TODO falta probar editar y eliminar
}