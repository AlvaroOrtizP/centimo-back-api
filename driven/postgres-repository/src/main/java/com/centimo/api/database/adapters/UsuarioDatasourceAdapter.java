package com.centimo.api.database.adapters;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centimo.api.database.mappers.UsuarioDatasourceMapper;
import com.centimo.api.database.models.UsuarioMO;
import com.centimo.api.database.repositories.UsuarioRepository;
import com.centimo.api.domain.models.Usuario;
import com.centimo.api.ports.driven.UsuarioDrivenPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioDatasourceAdapter implements UsuarioDrivenPort {

	private final UsuarioRepository repository;
	private final UsuarioDatasourceMapper mapper;
	private final CryptoService cryptoService;

	@Override
	@Transactional
	public Optional<Usuario> findByUsername(String username) {
		return repository.findByUsername(username).map(mo -> {
			decryptSecret(mo);
			return mapper.toDomain(mo);
		});
	}

	@Override
	@Transactional
	public Usuario guardar(Usuario usuario) {
		UsuarioMO mo = mapper.toEntity(usuario);
		if (mo.getTotpSecret() != null) {
			mo.setTotpSecret(cryptoService.encrypt(mo.getTotpSecret()));
		}
		UsuarioMO saved = repository.save(mo);
		decryptSecret(saved);
		return mapper.toDomain(saved);
	}

	@Override
	@Transactional
	public long count() {
		return repository.count();
	}

	private void decryptSecret(UsuarioMO mo) {
		if (mo.getTotpSecret() != null) {
			mo.setTotpSecret(cryptoService.decrypt(mo.getTotpSecret()));
		}
	}
}
