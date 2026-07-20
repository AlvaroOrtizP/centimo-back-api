package com.centimo.api.mappers;

import com.centimo.api.domain.models.ElementoListaTareas;
import com.centimo.api.dto.ElementoListaTareasDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ElementoListaTareasApiMapper {

  @Mapping(target = "text", source = "texto")
  @Mapping(target = "checked", source = "marcado")
  ElementoListaTareasDto toDto(ElementoListaTareas domain);
}
