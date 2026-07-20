package com.centimo.api.mappers;

import com.centimo.api.domain.models.AsignacionSalario;
import com.centimo.api.dto.AsignacionSalarioDto;
import com.centimo.api.dto.CreateAsignacionSalarioRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AsignacionSalarioApiMapper {

  @Mapping(target = "year", source = "anio")
  @Mapping(target = "month", source = "mes")
  @Mapping(target = "platformId", source = "plataformaId")
  @Mapping(target = "type", source = "tipo")
  @Mapping(target = "value", source = "valor")
  @Mapping(target = "note", source = "nota")
  AsignacionSalarioDto toDto(AsignacionSalario domain);

  @Mapping(target = "anio", source = "year")
  @Mapping(target = "mes", source = "month")
  @Mapping(target = "plataformaId", source = "platformId")
  @Mapping(target = "tipo", source = "type")
  @Mapping(target = "valor", source = "value")
  @Mapping(target = "nota", source = "note")
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  AsignacionSalario toDomain(CreateAsignacionSalarioRequest request);
}
