package com.centimo.api.database.mappers;

import com.centimo.api.database.models.GastoMO;
import com.centimo.api.domain.models.Gasto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GastoMapper {

  @Mapping(target = "instantaneaId", source = "instantanea.id")
  Gasto toDomain(GastoMO mo);

  GastoMO toMO(Gasto domain);
}
