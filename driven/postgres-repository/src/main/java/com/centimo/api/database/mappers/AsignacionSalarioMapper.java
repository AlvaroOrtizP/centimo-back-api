package com.centimo.api.database.mappers;

import com.centimo.api.database.models.AsignacionSalarioMO;
import com.centimo.api.domain.models.AsignacionSalario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AsignacionSalarioMapper {

  @Mapping(target = "plataformaId", source = "plataforma.id")
  AsignacionSalario toDomain(AsignacionSalarioMO mo);

  AsignacionSalarioMO toMO(AsignacionSalario domain);
}
