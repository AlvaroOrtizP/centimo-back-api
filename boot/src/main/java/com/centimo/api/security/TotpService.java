package com.centimo.api.security;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.centimo.api.ports.driven.TotpPort;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;

@Service
public class TotpService implements TotpPort {

	private static final String ISSUER = "Centimo";

	private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
	private final CodeVerifier verifier = new DefaultCodeVerifier(
			new DefaultCodeGenerator(), new SystemTimeProvider());

	@Override
	public String generateSecret() {
		return secretGenerator.generate();
	}

	@Override
	public boolean verify(String secret, String code) {
		return verifier.isValidCode(secret, code);
	}

	@Override
	public String getOtpAuthUrl(String secret, String account) {
		String issuer = URLEncoder.encode(ISSUER, StandardCharsets.UTF_8);
		String acc = URLEncoder.encode(account, StandardCharsets.UTF_8);
		return "otpauth://totp/" + issuer + ":" + acc
				+ "?secret=" + secret
				+ "&issuer=" + issuer
				+ "&algorithm=SHA1&digits=6&period=30";
	}

	@Override
	public List<String> generateBackupCodes() {
		SecureRandom rnd = new SecureRandom();
		List<String> codes = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			byte[] b = new byte[6];
			rnd.nextBytes(b);
			codes.add(bytesToHex(b));
		}
		return codes;
	}

	private String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}
