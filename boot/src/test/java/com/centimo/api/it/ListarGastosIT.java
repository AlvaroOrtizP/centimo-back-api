package com.centimo.api.it;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración del endpoint GET /expenses (listExpenses), con especial
 * atención al listado por periodo (year + month) que agrupa los gastos de todas
 * las instantáneas de ese mes.
 */
@AutoConfigureMockMvc(addFilters = false)
class ListarGastosIT extends AbstractIntegrationIT {

    private static final String PLATAFORMA_ID = "plataforma-it";
    private static final String CUENTA_1_ID = "cuenta-1";
    private static final String CUENTA_2_ID = "cuenta-2";
    private static final int ANIO = 2026;
    private static final int MES = 7;
    private static final String INSTANTANEA_1_ID = CUENTA_1_ID + "-" + ANIO + "-" + MES;
    private static final String INSTANTANEA_2_ID = CUENTA_2_ID + "-" + ANIO + "-" + MES;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        limpiar();
        jdbcTemplate.update(
            "INSERT INTO plataformas (id, nombre, tipo, color, icono, orden) VALUES (?, ?, ?, ?, ?, ?)",
            PLATAFORMA_ID, "Plataforma IT", "banco", "#004481", "building", 1);

        jdbcTemplate.update(
            "INSERT INTO cuentas (id, plataforma_id, nombre, tipo, moneda, orden) VALUES (?, ?, ?, ?, ?, ?)",
            CUENTA_1_ID, PLATAFORMA_ID, "Cuenta 1", "corriente", "EUR", 1);

        jdbcTemplate.update(
            "INSERT INTO cuentas (id, plataforma_id, nombre, tipo, moneda, orden) VALUES (?, ?, ?, ?, ?, ?)",
            CUENTA_2_ID, PLATAFORMA_ID, "Cuenta 2", "corriente", "EUR", 2);
    }

    private void insertarInstantanea(String id, String cuentaId) {
        jdbcTemplate.update(
            "INSERT INTO instantaneas_mensuales (id, cuenta_id, anio, mes, saldo, ingresos, gastos) VALUES (?, ?, ?, ?, ?, ?, ?)",
            id, cuentaId, ANIO, MES, 5000.00, 2200.00, 0.00);
    }

    private void insertarInstantaneaOtroPeriodo(String id, String cuentaId, int anio, int mes) {
        jdbcTemplate.update(
            "INSERT INTO instantaneas_mensuales (id, cuenta_id, anio, mes, saldo, ingresos, gastos) VALUES (?, ?, ?, ?, ?, ?, ?)",
            id, cuentaId, anio, mes, 5000.00, 2200.00, 0.00);
    }

    private void insertarGasto(String id, String instantaneaId, String categoria, double cantidad, String fecha, String descripcion) {
        jdbcTemplate.update(
            "INSERT INTO gastos (id, instantanea_id, categoria, cantidad, fecha, descripcion) VALUES (?, ?, ?, ?, ?, ?)",
            id, instantaneaId, categoria, cantidad, java.sql.Date.valueOf(fecha), descripcion);
    }

    @AfterEach
    void tearDown() {
        limpiar();
    }

    private void limpiar() {
        jdbcTemplate.update("TRUNCATE TABLE gastos, instantaneas_mensuales, cuentas, plataformas CASCADE");
    }

    @Nested
    @DisplayName("Listado por periodo (year + month)")
    class PorPeriodo {

        @Test
        @DisplayName("agrupa los gastos de todas las instantaneas del periodo")
        void agrupaGastosDeTodasLasInstantaneas() throws Exception {
            insertarInstantanea(INSTANTANEA_1_ID, CUENTA_1_ID);
            insertarInstantanea(INSTANTANEA_2_ID, CUENTA_2_ID);
            insertarGasto("g1", INSTANTANEA_1_ID, "Ocio", 5.0, "2026-07-13", "Suscripcion");
            insertarGasto("g2", INSTANTANEA_1_ID, "Comida", 20.0, "2026-07-02", null);
            insertarGasto("g3", INSTANTANEA_2_ID, "Trabajo", 10.0, "2026-07-01", "Transporte");

            mockMvc.perform(get("/expenses").param("year", String.valueOf(ANIO)).param("month", String.valueOf(MES)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[?(@.id == 'g1')].amount").value(5.0))
                .andExpect(jsonPath("$[?(@.id == 'g2')].amount").value(20.0))
                .andExpect(jsonPath("$[?(@.id == 'g3')].amount").value(10.0));
        }

        @Test
        @DisplayName("con instantanea sin gastos devuelve lista vacia")
        void periodoConInstantaneaSinGastosDevuelveVacio() throws Exception {
            insertarInstantanea(INSTANTANEA_1_ID, CUENTA_1_ID);

            mockMvc.perform(get("/expenses").param("year", String.valueOf(ANIO)).param("month", String.valueOf(MES)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("sin instantaneas en el periodo devuelve lista vacia")
        void periodoInexistenteDevuelveVacio() throws Exception {
            mockMvc.perform(get("/expenses").param("year", "2020").param("month", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("no devuelve gastos de otros periodos")
        void noMezclaGastosDeOtrosPeriodos() throws Exception {
            insertarInstantanea(INSTANTANEA_1_ID, CUENTA_1_ID);
            insertarInstantaneaOtroPeriodo(CUENTA_1_ID + "-2026-6", CUENTA_1_ID, 2026, 6);
            insertarGasto("g-otro", CUENTA_1_ID + "-2026-6", "Ocio", 99.0, "2026-06-10", null);
            insertarGasto("g-mes", INSTANTANEA_1_ID, "Ocio", 5.0, "2026-07-13", null);

            mockMvc.perform(get("/expenses").param("year", String.valueOf(ANIO)).param("month", String.valueOf(MES)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("g-mes"))
                .andExpect(jsonPath("$[0].amount").value(5.0));
        }
    }

    @Nested
    @DisplayName("Listado por snapshotId")
    class PorSnapshotId {

        @Test
        @DisplayName("solo devuelve los gastos de esa instantanea")
        void filtraPorInstantanea() throws Exception {
            insertarInstantanea(INSTANTANEA_1_ID, CUENTA_1_ID);
            insertarInstantanea(INSTANTANEA_2_ID, CUENTA_2_ID);
            insertarGasto("g1", INSTANTANEA_1_ID, "Ocio", 5.0, "2026-07-13", null);
            insertarGasto("g2", INSTANTANEA_2_ID, "Comida", 20.0, "2026-07-02", null);

            mockMvc.perform(get("/expenses").param("snapshotId", INSTANTANEA_1_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("g1"));
        }

        @Test
        @DisplayName("de una instantanea inexistente devuelve lista vacia")
        void instantaneaInexistenteDevuelveVacio() throws Exception {
            mockMvc.perform(get("/expenses").param("snapshotId", "no-existe-2026-7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("Sin parametros")
    class SinParametros {

        @Test
        @DisplayName("devuelve lista vacia")
        void sinParametrosDevuelveVacio() throws Exception {
            insertarInstantanea(INSTANTANEA_1_ID, CUENTA_1_ID);
            insertarGasto("g1", INSTANTANEA_1_ID, "Ocio", 5.0, "2026-07-13", null);

            mockMvc.perform(get("/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("Mapeo de campos")
    class MapeoCampos {

        @Test
        @DisplayName("devuelve los campos del gasto con la categoria en Title Case")
        void mapeaCamposCorrectamente() throws Exception {
            insertarInstantanea(INSTANTANEA_1_ID, CUENTA_1_ID);
            insertarGasto("g1", INSTANTANEA_1_ID, "Ocio", 5.0, "2026-07-13", "Suscripcion Discord");

            MvcResult result = mockMvc.perform(get("/expenses").param("snapshotId", INSTANTANEA_1_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("g1"))
                .andExpect(jsonPath("$[0].snapshotId").value(INSTANTANEA_1_ID))
                .andExpect(jsonPath("$[0].category").value("Ocio"))
                .andExpect(jsonPath("$[0].amount").value(5.0))
                .andExpect(jsonPath("$[0].date").value("2026-07-13"))
                .andExpect(jsonPath("$[0].description").value("Suscripcion Discord"))
                .andReturn();

            assertThat(result.getResponse().getContentAsString())
                .contains("\"category\":\"Ocio\"");
        }
    }
}
