package com.centimo.api.mappers;

import com.centimo.api.domain.models.Nomina;
import com.centimo.api.dto.NominaCreate;
import com.centimo.api.dto.NominaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NominaApiMapper {

  @Mapping(target = "year", source = "anio")
  @Mapping(target = "month", source = "mes")
  @Mapping(target = "value", source = "valor")
  @Mapping(target = "note", source = "nota")
  NominaResponse toNominaResponse(Nomina nomina);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "anio", source = "year")
  @Mapping(target = "mes", source = "month")
  @Mapping(target = "valor", source = "value")
  @Mapping(target = "nota", source = "note")
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  Nomina toDomain(NominaCreate request);
}
