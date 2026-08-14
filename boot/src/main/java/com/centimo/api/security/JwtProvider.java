package com.centimo.api.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.centimo.api.ports.driven.JwtPort;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;

@Service
public class JwtProvider implements JwtPort {

	private final SecretKey key;
	private final long sessionMillis;
	private final long preAuthMillis;

	public JwtProvider(
			@Value("${jwt.secret:}") String secret,
			@Value("${jwt.session-ttl-millis:86400000}") long sessionTtl,
			@Value("${jwt.preauth-ttl-millis:300000}") long preAuthTtl) {
		this.key = buildKey(secret);
		this.sessionMillis = sessionTtl;
		this.preAuthMillis = preAuthTtl;
	}

	private SecretKey buildKey(String secret) {
		if (secret == null || secret.length() < 32) {
			secret = "dev-only-insecure-secret-change-me-1234567890";
		}
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	@Override
	public String generateSessionToken(String username) {
		return build(username, "session", sessionMillis);
	}

	@Override
	public String generatePreAuthToken(String username) {
		return build(username, "preauth", preAuthMillis);
	}

	private String build(String username, String type, long ttl) {
		long now = System.currentTimeMillis();
		return Jwts.builder()
				.subject(username)
				.claim("typ", type)
				.issuedAt(new Date(now))
				.expiration(new Date(now + ttl))
				.signWith(key, Jwts.SIG.HS256)
				.compact();
	}

	@Override
	public String validateSessionToken(String token) {
		return parse(token, "session");
	}

	@Override
	public String validatePreAuthToken(String token) {
		return parse(token, "preauth");
	}

	private String parse(String token, String expectedType) {
		return Jwts.parser()
				.verifyWith(key)
				.require("typ", expectedType)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}
}
