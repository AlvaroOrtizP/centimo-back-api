package com.centimo.api.ports.driven;

import java.util.List;

public interface TotpPort {

	String generateSecret();

	String getOtpAuthUrl(String secret, String account);

	boolean verify(String secret, String code);

	List<String> generateBackupCodes();
}
