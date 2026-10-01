package com.centimo.api.mappers;

import com.centimo.api.domain.models.Nomina;
import com.centimo.api.dto.NominaRequest;
import com.centimo.api.dto.NominaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NominaApiMapper {

  NominaResponse toNominaResponse(Nomina nomina);

  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  Nomina toDomain(NominaRequest request);
}