package com.centimo.api.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.MockMvc;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.Replace.ANY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		properties = { "app.user.password=Moneda2?" })
@AutoConfigureEmbeddedDatabase(type = POSTGRES, provider = ZONKY, replace = ANY)
@AutoConfigureMockMvc(addFilters = true)
@ExtendWith(OutputCaptureExtension.class)
class AuthIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void loginConPasswordCorrectoDevuelveToken(CapturedOutput output) throws Exception {
		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ \"username\": \"admin\", \"password\": \"Moneda2?\" }"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists())
				.andExpect(jsonPath("$.requires2fa").value(false));
	}

	@Test
	void loginConPasswordIncorrectoDevuelve401(CapturedOutput output) throws Exception {
		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ \"username\": \"admin\", \"password\": \"bad\" }"))
				.andExpect(status().isUnauthorized());
	}
}
