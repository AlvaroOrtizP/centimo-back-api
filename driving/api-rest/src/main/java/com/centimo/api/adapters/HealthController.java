package com.centimo.api.adapters;

import com.centimo.api.HealthApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController implements HealthApi {

	@Override
	public ResponseEntity<Void> healthCheck() {
		return ResponseEntity.ok().build();
	}
}
