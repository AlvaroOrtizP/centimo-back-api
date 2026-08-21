package com.centimo.api.domain.models;

import java.time.LocalDateTime;
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
public class Usuario {

	private String id;
	private String username;
	private String passwordHash;
	private String totpSecret;
	private boolean totpEnabled;
	private List<String> backupCodes;
	private LocalDateTime createdAt;
}
