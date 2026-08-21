package com.centimo.api.adapters;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import com.centimo.api.AuthApi;
import com.centimo.api.domain.models.LoginResult;
import com.centimo.api.domain.models.TotpSetupResult;
import com.centimo.api.dto.Confirm2faRequest;
import com.centimo.api.dto.Disable2faRequest;
import com.centimo.api.dto.LoginRequest;
import com.centimo.api.dto.LoginResponse;
import com.centimo.api.dto.TotpSetupResponse;
import com.centimo.api.dto.Verify2faRequest;
import com.centimo.api.mappers.AuthApiMapper;
import com.centimo.api.ports.driving.AuthDrivingPort;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

	private final AuthDrivingPort authDrivingPort;
	private final AuthApiMapper mapper;

	@Override
	public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
		LoginResult result = authDrivingPort.login(loginRequest.getUsername(), loginRequest.getPassword());
		return ResponseEntity.ok(mapper.toLoginResponse(result));
	}

	@Override
	public ResponseEntity<LoginResponse> verify2fa(Verify2faRequest verify2faRequest) {
		String token = authDrivingPort.verify2fa(verify2faRequest.getPreAuthToken(), verify2faRequest.getCode());
		return ResponseEntity.ok(new LoginResponse().token(token).requires2fa(false));
	}

	@Override
	public ResponseEntity<TotpSetupResponse> setup2fa() {
		TotpSetupResult result = authDrivingPort.setup2fa(currentUsername());
		return ResponseEntity.ok(mapper.toTotpSetupResponse(result));
	}

	@Override
	public ResponseEntity<Void> confirm2fa(Confirm2faRequest confirm2faRequest) {
		authDrivingPort.confirm2fa(currentUsername(), confirm2faRequest.getCode());
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> disable2fa(Disable2faRequest disable2faRequest) {
		authDrivingPort.disable2fa(currentUsername(), disable2faRequest.getPassword());
		return ResponseEntity.noContent().build();
	}

	private String currentUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
