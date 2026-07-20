package com.centimo.api.database.mappers;

import com.centimo.api.database.models.ElementoListaTareasMO;
import com.centimo.api.domain.models.ElementoListaTareas;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ElementoListaTareasMapper {

  @Mapping(target = "instantaneaId", source = "instantanea.id")
  ElementoListaTareas toDomain(ElementoListaTareasMO mo);

  ElementoListaTareasMO toMO(ElementoListaTareas domain);
}
