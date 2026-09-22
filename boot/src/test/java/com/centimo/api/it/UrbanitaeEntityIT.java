package com.centimo.api.it;

import com.centimo.api.it.support.StatisticsAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/it/urbanitae/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Urbanitae — CRUD de balances mensuales y registro de compras")
class UrbanitaeEntityIT extends AbstractIntegrationIT {

  private static final String BALANCE_BASE = "/api/v1/urbanitae/balances";
  private static final String COMPRA_BASE = "/api/v1/urbanitae/compras";
  private static final String ENTIDAD_BALANCE = "com.centimo.api.database.models.UrbanitaeBalanceMO";
  private static final String ENTIDAD_COMPRA = "com.centimo.api.database.models.UrbanitaeCompraMO";

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Nested
  @DisplayName("POST /urbanitae/balances (upsert por mes)")
  class CrearBalance {

    @Test
    @DisplayName("crea la fila con id natural {AAAA-MM} y aplica defaults en campos opcionales")
    void creaConIdNaturalYDefaults() throws Exception {
      mockMvc.perform(post(BALANCE_BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026-01",
                    "balanceMensual": 100.50
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("2026-01"))
          .andExpect(jsonPath("$.mes").value("2026-01"))
          .andExpect(jsonPath("$.balanceMensual").value(100.5))
          .andExpect(jsonPath("$.aporteMensual").value(0.0))
          .andExpect(jsonPath("$.dineroTotal").value(0.0))
          .andExpect(jsonPath("$.dineroHacienda").value(0.0))
          .andExpect(jsonPath("$.dineroFinal").value(0.0));

      var row = jdbcTemplate.queryForMap(
          "SELECT mes, balance_mensual, aporte_mensual, dinero_total, dinero_hacienda, dinero_final "
              + "FROM urbanitae_balances WHERE id = ?",
          "2026-01");
      assertThat(row.get("mes")).isEqualTo("2026-01");
      assertThat((BigDecimal) row.get("balance_mensual")).isEqualByComparingTo("100.50");
      assertThat((BigDecimal) row.get("aporte_mensual")).isEqualByComparingTo("0.00");
      assertThat((BigDecimal) row.get("dinero_total")).isEqualByComparingTo("0.00");
      assertThat((BigDecimal) row.get("dinero_hacienda")).isEqualByComparingTo("0.00");
      assertThat((BigDecimal) row.get("dinero_final")).isEqualByComparingTo("0.00");

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD_BALANCE)
          .hasInsertCount(1)
          .verify();
    }

    @Test
    @DisplayName("respeta los opcionales enviados")
    void respetaOpcionales() throws Exception {
      mockMvc.perform(post(BALANCE_BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026-02",
                    "balanceMensual": 1000.00,
                    "aporteMensual": 200.00,
                    "dineroTotal": 15.00,
                    "dineroHacienda": 2.85,
                    "dineroFinal": 12.15
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("2026-02"))
          .andExpect(jsonPath("$.aporteMensual").value(200.0))
          .andExpect(jsonPath("$.dineroTotal").value(15.0))
          .andExpect(jsonPath("$.dineroHacienda").value(2.85))
          .andExpect(jsonPath("$.dineroFinal").value(12.15));
    }

    @Test
    @DisplayName("repetir mes actualiza la fila sin duplicar y conserva fecha_creacion")
    void upsertNoDuplica() throws Exception {
      crearBalance("2026-03", 10.00);

      var fechaCreacionOriginal = jdbcTemplate.queryForObject(
          "SELECT fecha_creacion FROM urbanitae_balances WHERE id = ?", Object.class, "2026-03");

      mockMvc.perform(post(BALANCE_BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026-03",
                    "balanceMensual": 99.99
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("2026-03"))
          .andExpect(jsonPath("$.balanceMensual").value(99.99));

      assertThat(countBalances()).isEqualTo(1);
      assertThat(jdbcTemplate.queryForObject(
          "SELECT balance_mensual FROM urbanitae_balances WHERE id = ?", BigDecimal.class, "2026-03"))
          .isEqualByComparingTo("99.99");
      assertThat(jdbcTemplate.queryForObject(
          "SELECT fecha_creacion FROM urbanitae_balances WHERE id = ?", Object.class, "2026-03"))
          .isEqualTo(fechaCreacionOriginal);

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD_BALANCE)
          .hasInsertCount(1)
          .hasUpdateCount(1)
          .hasLoadCount(1)
          .verify();
    }
  }

  @Nested
  @DisplayName("GET /urbanitae/balances")
  class ListarBalances {

    @Test
    @DisplayName("por defecto devuelve todos los balances ordenados por mes descendente")
    void listaDescendentePorDefecto() throws Exception {
      crearBalance("2025-11", 1.00);
      crearBalance("2025-12", 2.00);
      crearBalance("2026-01", 3.00);

      mockMvc.perform(get(BALANCE_BASE))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(3))
          .andExpect(jsonPath("$[0].mes").value("2026-01"))
          .andExpect(jsonPath("$[1].mes").value("2025-12"))
          .andExpect(jsonPath("$[2].mes").value("2025-11"));
    }

    @Test
    @DisplayName("order=asc invierte el orden y limit recorta el resultado")
    void listaAscendenteConLimite() throws Exception {
      crearBalance("2025-11", 1.00);
      crearBalance("2025-12", 2.00);
      crearBalance("2026-01", 3.00);

      mockMvc.perform(get(BALANCE_BASE).param("order", "asc").param("limit", "2"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[0].mes").value("2025-11"))
          .andExpect(jsonPath("$[1].mes").value("2025-12"));
    }

    @Test
    @DisplayName("sin datos devuelve lista vacía")
    void listaVacia() throws Exception {
      mockMvc.perform(get(BALANCE_BASE))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(0));
    }
  }

  @Nested
  @DisplayName("PUT /urbanitae/balances/{id}")
  class ActualizarBalance {

    @Test
    @DisplayName("actualiza los campos enviados y conserva los opcionales omitidos")
    void actualizaCamposEnviados() throws Exception {
      crearBalance("2026-04", 100.00, 200.00, 15.00, 2.85, 12.15);

      mockMvc.perform(put(BALANCE_BASE + "/2026-04")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "mes": "2026-04",
                    "balanceMensual": 123.45
                  }
                  """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("2026-04"))
          .andExpect(jsonPath("$.balanceMensual").value(123.45))
          .andExpect(jsonPath("$.aporteMensual").value(200.0))
          .andExpect(jsonPath("$.dineroTotal").value(15.0))
          .andExpect(jsonPath("$.dineroHacienda").value(2.85))
          .andExpect(jsonPath("$.dineroFinal").value(12.15));

      assertThat(countBalances()).isEqualTo(1);
    }
  }

  @Nested
  @DisplayName("DELETE /urbanitae/balances/{id}")
  class EliminarBalance {

    @Test
    @DisplayName("elimina la fila indicada")
    void elimina() throws Exception {
      crearBalance("2026-06", 10.00);
      assertThat(countBalances()).isEqualTo(1);

      mockMvc.perform(delete(BALANCE_BASE + "/2026-06"))
          .andExpect(status().isNoContent());

      assertThat(countBalances()).isZero();

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD_BALANCE)
          .hasInsertCount(1)
          .hasDeleteCount(1)
          .hasLoadCount(1)
          .verify();
    }
  }

  @Nested
  @DisplayName("POST /urbanitae/compras")
  class CrearCompra {

    @Test
    @DisplayName("crea la compra con UUID y aplica default al rendimiento")
    void creaConUuidYDefaultRendimiento() throws Exception {
      String id = mockMvc.perform(post(COMPRA_BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "fecha": "2026-03-01",
                    "entidad": "Altamar",
                    "monto": 5000.00,
                    "estado": "activa"
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.entidad").value("Altamar"))
          .andExpect(jsonPath("$.fecha").value("2026-03-01"))
          .andExpect(jsonPath("$.monto").value(5000.0))
          .andExpect(jsonPath("$.rendimiento").value(0.0))
          .andExpect(jsonPath("$.estado").value("activa"))
          .andReturn().getResponse().getContentAsString();

      assertThat(id).contains("\"id\":");
      assertThat(countCompras()).isEqualTo(1);

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD_COMPRA)
          .hasInsertCount(1)
          .verify();
    }

    @Test
    @DisplayName("respeta estado, rendimiento y monto enviados")
    void respetaDatosEnviados() throws Exception {
      mockMvc.perform(post(COMPRA_BASE)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "fecha": "2026-04-15",
                    "entidad": "Lios",
                    "monto": 1000.00,
                    "rendimiento": 8.50,
                    "estado": "activa"
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.entidad").value("Lios"))
          .andExpect(jsonPath("$.monto").value(1000.0))
          .andExpect(jsonPath("$.rendimiento").value(8.5))
          .andExpect(jsonPath("$.estado").value("activa"));
    }
  }

  @Nested
  @DisplayName("GET /urbanitae/compras")
  class ListarCompras {

    @Test
    @DisplayName("listar todas y filtrar por estado")
    void listarYFiltrarPorEstado() throws Exception {
      crearCompra("2026-01-10", "A", 100.00, "activa");
      crearCompra("2026-02-10", "B", 200.00, "vendida");
      crearCompra("2026-03-10", "C", 300.00, "activa");

      mockMvc.perform(get(COMPRA_BASE))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(3));

      mockMvc.perform(get(COMPRA_BASE).param("estado", "activa"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("order=asc ordena por fecha ascendente")
    void ordenAscendente() throws Exception {
      crearCompra("2026-02-10", "B", 200.00, "activa");
      crearCompra("2026-01-10", "A", 100.00, "activa");

      mockMvc.perform(get(COMPRA_BASE).param("order", "asc"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[0].entidad").value("A"))
          .andExpect(jsonPath("$[1].entidad").value("B"));
    }

    @Test
    @DisplayName("estado inválido devuelve 400")
    void estadoInvalido() throws Exception {
      mockMvc.perform(get(COMPRA_BASE).param("estado", "inexistente"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("PUT /urbanitae/compras/{id}")
  class ActualizarCompra {

    @Test
    @DisplayName("permite marcar una compra como vendida conservando el resto")
    void marcarComoVendida() throws Exception {
      crearCompra("2026-05-10", "Altamar", 750.00, "activa", 7.50);

      String id = jdbcTemplate.queryForObject(
          "SELECT id FROM urbanitae_compras WHERE entidad = 'Altamar'", String.class);

      mockMvc.perform(put(COMPRA_BASE + "/" + id)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "estado": "vendida"
                  }
                  """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.estado").value("vendida"))
          .andExpect(jsonPath("$.entidad").value("Altamar"))
          .andExpect(jsonPath("$.monto").value(750.0))
          .andExpect(jsonPath("$.rendimiento").value(7.5));

      assertThat(jdbcTemplate.queryForObject(
          "SELECT estado FROM urbanitae_compras WHERE id = ?", String.class, id))
          .isEqualTo("vendida");
    }
  }

  @Nested
  @DisplayName("DELETE /urbanitae/compras/{id}")
  class EliminarCompra {

    @Test
    @DisplayName("elimina la compra indicada")
    void elimina() throws Exception {
      crearCompra("2026-06-10", "Lios", 500.00, "activa");
      assertThat(countCompras()).isEqualTo(1);

      String id = jdbcTemplate.queryForObject(
          "SELECT id FROM urbanitae_compras WHERE entidad = 'Lios'", String.class);

      mockMvc.perform(delete(COMPRA_BASE + "/" + id))
          .andExpect(status().isNoContent());

      assertThat(countCompras()).isZero();

      StatisticsAssert.assertThat(statistics())
          .forEntity(ENTIDAD_COMPRA)
          .hasInsertCount(1)
          .hasDeleteCount(1)
          .hasLoadCount(1)
          .verify();
    }
  }

  private void crearBalance(String mes, double balance) throws Exception {
    mockMvc.perform(post(BALANCE_BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"mes":"%s","balanceMensual":%s}
                """.formatted(mes, balance)))
        .andExpect(status().isCreated());
  }

  private void crearBalance(String mes, double balance, double aporte, double total,
                            double hacienda, double dineroFinal) throws Exception {
    mockMvc.perform(post(BALANCE_BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"mes":"%s","balanceMensual":%s,"aporteMensual":%s,"dineroTotal":%s,
                 "dineroHacienda":%s,"dineroFinal":%s}
                """.formatted(mes, balance, aporte, total, hacienda, dineroFinal)))
        .andExpect(status().isCreated());
  }

  private void crearCompra(String fecha, String entidad, double monto, String estado) throws Exception {
    mockMvc.perform(post(COMPRA_BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"fecha":"%s","entidad":"%s","monto":%s,"estado":"%s"}
                """.formatted(fecha, entidad, monto, estado)))
        .andExpect(status().isCreated());
  }

  private void crearCompra(String fecha, String entidad, double monto, String estado, double rendimiento) throws Exception {
    mockMvc.perform(post(COMPRA_BASE)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"fecha":"%s","entidad":"%s","monto":%s,"estado":"%s","rendimiento":%s}
                """.formatted(fecha, entidad, monto, estado, rendimiento)))
        .andExpect(status().isCreated());
  }

  private int countBalances() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM urbanitae_balances", Integer.class);
  }

  private int countCompras() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM urbanitae_compras", Integer.class);
  }
}