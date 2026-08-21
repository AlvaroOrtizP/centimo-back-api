package com.centimo.api.domain.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotpSetupResult {

	private String secret;
	private String otpauthUrl;
	private List<String> backupCodes;
}
