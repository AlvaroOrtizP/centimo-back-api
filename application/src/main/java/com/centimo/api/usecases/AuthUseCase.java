package com.centimo.api.usecases;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centimo.api.domain.exceptions.AuthException;
import com.centimo.api.domain.models.LoginResult;
import com.centimo.api.domain.models.TotpSetupResult;
import com.centimo.api.domain.models.Usuario;
import com.centimo.api.ports.driving.AuthDrivingPort;
import com.centimo.api.ports.driven.JwtPort;
import com.centimo.api.ports.driven.PasswordPort;
import com.centimo.api.ports.driven.TotpPort;
import com.centimo.api.ports.driven.UsuarioDrivenPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthUseCase implements AuthDrivingPort {

	private final UsuarioDrivenPort usuarioDrivenPort;
	private final JwtPort jwtPort;
	private final TotpPort totpPort;
	private final PasswordPort passwordPort;

	@Override
	@Transactional
	public LoginResult login(String username, String password) {
		Usuario usuario = usuarioDrivenPort.findByUsername(username)
				.orElseThrow(() -> new AuthException("Credenciales inválidas"));
		if (!passwordPort.matches(password, usuario.getPasswordHash())) {
			throw new AuthException("Credenciales inválidas");
		}
		if (usuario.isTotpEnabled()) {
			String preAuthToken = jwtPort.generatePreAuthToken(username);
			return LoginResult.builder().preAuthToken(preAuthToken).requires2fa(true).build();
		}
		String token = jwtPort.generateSessionToken(username);
		return LoginResult.builder().token(token).requires2fa(false).build();
	}

	@Override
	@Transactional
	public String verify2fa(String preAuthToken, String code) {
		String username = jwtPort.validatePreAuthToken(preAuthToken);
		Usuario usuario = usuarioDrivenPort.findByUsername(username)
				.orElseThrow(() -> new AuthException("Usuario no encontrado"));
		String secret = usuario.getTotpSecret();
		if (secret == null) {
			throw new AuthException("2FA no configurado");
		}
		if (totpPort.verify(secret, code)) {
			return jwtPort.generateSessionToken(username);
		}
		if (usuario.getBackupCodes() != null && matchesBackupCode(code, usuario.getBackupCodes())) {
			return jwtPort.generateSessionToken(username);
		}
		throw new AuthException("Código 2FA inválido");
	}

	@Override
	@Transactional
	public TotpSetupResult setup2fa(String username) {
		Usuario usuario = usuarioDrivenPort.findByUsername(username)
				.orElseThrow(() -> new AuthException("Usuario no encontrado"));
		String secret = totpPort.generateSecret();
		List<String> plainBackupCodes = totpPort.generateBackupCodes();
		List<String> hashedBackupCodes = plainBackupCodes.stream().map(passwordPort::encode).toList();
		usuario.setTotpSecret(secret);
		usuario.setBackupCodes(hashedBackupCodes);
		usuario.setTotpEnabled(false);
		usuarioDrivenPort.guardar(usuario);
		String otpauthUrl = totpPort.getOtpAuthUrl(secret, username);
		return TotpSetupResult.builder()
				.secret(secret)
				.otpauthUrl(otpauthUrl)
				.backupCodes(plainBackupCodes)
				.build();
	}

	@Override
	@Transactional
	public void confirm2fa(String username, String code) {
		Usuario usuario = usuarioDrivenPort.findByUsername(username)
				.orElseThrow(() -> new AuthException("Usuario no encontrado"));
		String secret = usuario.getTotpSecret();
		if (secret == null) {
			throw new AuthException("2FA no iniciado");
		}
		if (!totpPort.verify(secret, code)) {
			throw new AuthException("Código 2FA inválido");
		}
		usuario.setTotpEnabled(true);
		usuarioDrivenPort.guardar(usuario);
	}

	@Override
	@Transactional
	public void disable2fa(String username, String password) {
		Usuario usuario = usuarioDrivenPort.findByUsername(username)
				.orElseThrow(() -> new AuthException("Usuario no encontrado"));
		if (!passwordPort.matches(password, usuario.getPasswordHash())) {
			throw new AuthException("Credenciales inválidas");
		}
		usuario.setTotpSecret(null);
		usuario.setBackupCodes(null);
		usuario.setTotpEnabled(false);
		usuarioDrivenPort.guardar(usuario);
	}

	private boolean matchesBackupCode(String code, List<String> hashedCodes) {
		return hashedCodes.stream().anyMatch(h -> passwordPort.matches(code, h));
	}
}
