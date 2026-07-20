package com.centimo.api.database.mappers;

import com.centimo.api.database.models.InversionCrowdlendingMO;
import com.centimo.api.domain.models.InversionCrowdlending;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InversionCrowdlendingMapper {

  @Mapping(target = "plataformaId", source = "plataforma.id")
  InversionCrowdlending toDomain(InversionCrowdlendingMO mo);

  InversionCrowdlendingMO toMO(InversionCrowdlending domain);
}
