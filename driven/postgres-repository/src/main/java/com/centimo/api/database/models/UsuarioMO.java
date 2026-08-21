package com.centimo.api.database.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioMO {

	@Id
	@Column(length = 50)
	private String id;

	@Column(nullable = false, unique = true, length = 100)
	private String username;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Column(name = "totp_secret")
	private String totpSecret;

	@Column(name = "totp_enabled", nullable = false)
	private boolean totpEnabled;

	@Column(name = "backup_codes", columnDefinition = "text")
	private String backupCodes;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;
}
