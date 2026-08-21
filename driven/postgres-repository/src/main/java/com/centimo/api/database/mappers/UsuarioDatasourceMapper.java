package com.centimo.api.database.mappers;

import java.util.Arrays;
import java.util.List;

import org.mapstruct.Mapper;

import com.centimo.api.database.models.UsuarioMO;
import com.centimo.api.domain.models.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioDatasourceMapper {

	Usuario toDomain(UsuarioMO mo);

	UsuarioMO toEntity(Usuario usuario);

	default String codesToCsv(List<String> codes) {
		return codes == null ? null : String.join(",", codes);
	}

	default List<String> csvToCodes(String csv) {
		return (csv == null || csv.isBlank()) ? null : Arrays.asList(csv.split(","));
	}
}
