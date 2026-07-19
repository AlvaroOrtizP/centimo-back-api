package com.centimo.api.mappers;

import com.centimo.api.domain.models.Plataforma;
import com.centimo.api.dto.CreatePlataformaRequest;
import com.centimo.api.dto.PlataformaDto;
import com.centimo.api.dto.UpdatePlataformaRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlataformaApiMapper {

  @Mapping(target = "name", source = "nombre")
  @Mapping(target = "type", source = "tipo")
  @Mapping(target = "icon", source = "icono")
  @Mapping(target = "order", source = "orden")
  @Mapping(target = "fixedNotes", source = "notasFijas")
  PlataformaDto toDto(Plataforma domain);

  @Mapping(target = "nombre", source = "name")
  @Mapping(target = "tipo", source = "type")
  @Mapping(target = "icono", source = "icon")
  @Mapping(target = "orden", source = "order")
  @Mapping(target = "notasFijas", source = "fixedNotes")
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  Plataforma toDomain(CreatePlataformaRequest request);

  @Mapping(target = "nombre", source = "name")
  @Mapping(target = "tipo", source = "type")
  @Mapping(target = "icono", source = "icon")
  @Mapping(target = "orden", source = "order")
  @Mapping(target = "notasFijas", source = "fixedNotes")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  Plataforma toDomain(UpdatePlataformaRequest request);
}
