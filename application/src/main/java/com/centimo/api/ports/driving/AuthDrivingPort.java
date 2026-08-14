package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.LoginResult;
import com.centimo.api.domain.models.TotpSetupResult;

public interface AuthDrivingPort {

	LoginResult login(String username, String password);

	String verify2fa(String preAuthToken, String code);

	TotpSetupResult setup2fa(String username);

	void confirm2fa(String username, String code);

	void disable2fa(String username, String password);
}
