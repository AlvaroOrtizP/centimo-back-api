package com.centimo.api.database.mappers;

import com.centimo.api.database.models.PlataformaMO;
import com.centimo.api.domain.models.Plataforma;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PlataformaMapper {

  Plataforma toDomain(PlataformaMO mo);

  PlataformaMO toMO(Plataforma domain);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  void updateMO(Plataforma domain, @MappingTarget PlataformaMO mo);
}
