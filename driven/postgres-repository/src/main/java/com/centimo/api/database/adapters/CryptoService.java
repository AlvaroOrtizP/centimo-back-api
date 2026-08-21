package com.centimo.api.database.adapters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

@Service
public class CryptoService {

	private final TextEncryptor encryptor;

	public CryptoService(
			@Value("${totp.master-key:change-me-in-prod-1234567890}") String masterKey,
			@Value("${totp.salt:deadbeefdeadbeef}") String salt) {
		this.encryptor = Encryptors.text(masterKey, salt);
	}

	public String encrypt(String plain) {
		return encryptor.encrypt(plain);
	}

	public String decrypt(String encrypted) {
		return encryptor.decrypt(encrypted);
	}
}
