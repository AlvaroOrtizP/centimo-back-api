package com.centimo.api.ports.driven;

import java.util.Optional;

import com.centimo.api.domain.models.Usuario;

public interface UsuarioDrivenPort {

	Optional<Usuario> findByUsername(String username);

	Usuario guardar(Usuario usuario);

	long count();
}
