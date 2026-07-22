package com.centimo.api.it;

import com.centimo.api.database.models.PlataformaMO;
import com.centimo.api.it.support.StatisticsAssert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FlujoCompletoIT extends AbstractIntegrationIT {
    /*@Test
    @Order(1)
    void crearPlataformaBase(CapturedOutput output) throws Exception {
        mockMvc.perform(post("/plataformas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content()
                        .andExpect(status().isCreated()));

                StatisticsAssert.assertThat(statistics())
                        .forEntity(PlataformaMO.class).hasInsertCount(1)
                        .verify();
    }*/
    /**
     * Hacer un select count de todas las entidades y comprobar que no existen datos en ninguna salvo Cuentas, Plataformas y flyway
     *
     *
     *
     */
}
