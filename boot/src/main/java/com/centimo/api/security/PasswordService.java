package com.centimo.api.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.centimo.api.ports.driven.PasswordPort;

@Service
public class PasswordService implements PasswordPort {

	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Override
	public String encode(String raw) {
		return encoder.encode(raw);
	}

	@Override
	public boolean matches(String raw, String encoded) {
		return encoder.matches(raw, encoded);
	}
}
