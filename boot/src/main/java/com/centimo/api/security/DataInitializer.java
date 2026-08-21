package com.centimo.api.security;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.centimo.api.domain.models.Usuario;
import com.centimo.api.ports.driven.PasswordPort;
import com.centimo.api.ports.driven.UsuarioDrivenPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Order(0)
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

	private final UsuarioDrivenPort usuarioDrivenPort;
	private final PasswordPort passwordPort;

	@Value("${app.user.username:admin}")
	private String username;

	@Value("${app.user.password:}")
	private String password;

	@Override
	public void run(ApplicationArguments args) {
		Usuario usuario = usuarioDrivenPort.findByUsername(username).orElse(null);

		if (usuario == null) {
			usuario = Usuario.builder()
					.id(UUID.randomUUID().toString())
					.username(username)
					.totpEnabled(false)
					.build();
		}

		String pwd = (password == null || password.isBlank())
				? UUID.randomUUID().toString().substring(0, 12)
				: password;

		usuario.setPasswordHash(passwordPort.encode(pwd));
		usuario.setTotpSecret(null);
		usuario.setBackupCodes(null);
		usuario.setTotpEnabled(false);
		usuarioDrivenPort.guardar(usuario);

		if (password == null || password.isBlank()) {
			log.warn("=========================================================");
			log.warn("Usuario inicial '{}' creado con contraseña generada:", username);
			log.warn("   {}", pwd);
			log.warn("Define APP_USER_PASSWORD en producción para fijarla.");
			log.warn("=========================================================");
		} else {
			log.info("Usuario inicial '{}' inicializado con la contraseña de configuración.", username);
		}
	}
}
