package com.centimo.api.it;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GastoIT extends AbstractIntegrationIT {

    /**
     * Test de integracion de la pantalla gastos:
     * 1. Comprobar tablas vacias
     * 2. Crear plataforma y cuenta
     * 3. Crear instantanea para ese anio/mes
     * 4. Listar gastos: da 0
     * 5. Crear varios gastos, comprobar que se suman en la instantanea
     * 6. Listar gastos: da 3
     * 7. Editar un gasto, comprobar recalculo en instantanea
     * 8. Eliminar un gasto, comprobar recalculo en instantanea
     */

    private static final String PLATAFORMA_ID = "bbva-it";
    private static final String CUENTA_ID = "bbva-it-nomina";
    private static final String INSTANTANEA_ID = CUENTA_ID + "-2026-7";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Order(1)
    void tablasVacias(CapturedOutput capturedOutput) throws Exception {
        Integer gastosCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM gastos", Integer.class);
        Integer instantaneasCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instantaneas_mensuales", Integer.class);

        assertThat(gastosCount).isZero();
        assertThat(instantaneasCount).isZero();
    }

    @Test
    @Order(2)
    void crearPlataformaYCuenta(CapturedOutput capturedOutput) throws Exception {
        jdbcTemplate.update(
            "INSERT INTO plataformas (id, nombre, tipo, color, icono, orden) VALUES (?, ?, ?, ?, ?, ?)",
            PLATAFORMA_ID, "BBVA IT", "banco", "#004481", "building", 1);

        jdbcTemplate.update(
            "INSERT INTO cuentas (id, plataforma_id, nombre, tipo, moneda, orden) VALUES (?, ?, ?, ?, ?, ?)",
            CUENTA_ID, PLATAFORMA_ID, "Nomina IT", "corriente", "EUR", 1);
    }

    @Test
    @Order(3)
    void crearSnapshot(CapturedOutput capturedOutput) throws Exception {
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
    }

    @Test
    @Order(4)
    void listarGastosVacio(CapturedOutput capturedOutput) throws Exception {
        mockMvc.perform(get("/expenses").param("snapshotId", INSTANTANEA_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Order(5)
    void crearGastos(CapturedOutput capturedOutput) throws Exception {
        // Llamada a createExpense: gasto de 20 para el día 2
        mockMvc.perform(post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "snapshotId": "%s",
                      "category": "Comida",
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
                      "category": "Ocio",
                      "amount": 5.0,
                      "date": "2026-07-13"
                    }
                    """.formatted(INSTANTANEA_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.amount").value(5.0));

        // Llamada a createExpense: gasto de 6.23 para el día 13
        mockMvc.perform(post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "snapshotId": "%s",
                      "category": "Ocio",
                      "amount": 6.23,
                      "date": "2026-07-13"
                    }
                    """.formatted(INSTANTANEA_ID)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.amount").value(6.23));

        // Comprobar que la instantánea tiene gastos totales de 31.23
        Float gastosTotal = jdbcTemplate.queryForObject(
            "SELECT gastos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(gastosTotal).isEqualTo(31.23f);
    }

    @Test
    @Order(6)
    void listarGastos(CapturedOutput capturedOutput) throws Exception {
        mockMvc.perform(get("/expenses").param("snapshotId", INSTANTANEA_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @Order(7)
    void editarGasto(CapturedOutput capturedOutput) throws Exception {
        String listaResponse = mockMvc.perform(get("/expenses").param("snapshotId", INSTANTANEA_ID))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        String primerGastoId = JsonPath.read(listaResponse, "$[0].id");

        // Editar el primer gasto: cambiar amount de 20.0 a 25.0
        mockMvc.perform(put("/expenses/{id}", primerGastoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "category": "Comida",
                      "amount": 25.0,
                      "date": "2026-07-02"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(primerGastoId))
            .andExpect(jsonPath("$.amount").value(25.0));

        // Comprobar que la instantánea se ha recalculado: 25 + 5 + 6.23 = 36.23
        Float gastosAfterEdit = jdbcTemplate.queryForObject(
            "SELECT gastos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(gastosAfterEdit).isEqualTo(36.23f);

        // Comprobar que hay 3 registros en gastos y 1 en instantanea
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM gastos", Integer.class)).isEqualTo(3);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM instantaneas_mensuales", Integer.class)).isOne();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plataformas", Integer.class)).isOne();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cuentas", Integer.class)).isOne();

        // Comprobar que el resto de tablas siguen vacías
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM posiciones_inversion", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM operaciones_inversion", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM inversiones_crowdlending", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM nomina", Integer.class)).isZero();
    }

    @Test
    @Order(8)
    void eliminarGasto(CapturedOutput capturedOutput) throws Exception {
        String listaResponse = mockMvc.perform(get("/expenses").param("snapshotId", INSTANTANEA_ID))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        List<String> ids = JsonPath.read(listaResponse, "$[?(@.amount == 25.0)].id");
        String gastoEditadoId = ids.get(0);

        mockMvc.perform(delete("/expenses/{id}", gastoEditadoId).param("snapshotId", INSTANTANEA_ID))
            .andExpect(status().isNoContent());

        // Comprobar que la instantánea se ha recalculado: 5 + 6.23 = 11.23
        Float gastosAfterDelete = jdbcTemplate.queryForObject(
            "SELECT gastos FROM instantaneas_mensuales WHERE cuenta_id = ? AND anio = ? AND mes = ?",
            Float.class, CUENTA_ID, 2026, 7);
        assertThat(gastosAfterDelete).isEqualTo(11.23f);

        // Listar gastos de la instantánea (debe devolver 2 registros)
        mockMvc.perform(get("/expenses").param("snapshotId", INSTANTANEA_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2));

        // Comprobar que hay 2 registros en gastos
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM gastos", Integer.class)).isEqualTo(2);
    }

    @Test
    @Order(9)
    void listarGastosPorPeriodo(CapturedOutput capturedOutput) throws Exception {
        // El periodo 2026/7 conserva los 2 gastos restantes tras la eliminación
        mockMvc.perform(get("/expenses").param("year", "2026").param("month", "7"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2));

        // Periodo sin instantáneas ni gastos
        mockMvc.perform(get("/expenses").param("year", "2025").param("month", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }
}
