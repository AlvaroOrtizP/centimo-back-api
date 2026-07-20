package com.centimo.api.database.mappers;

import com.centimo.api.database.models.PosicionInversionMO;
import com.centimo.api.domain.models.PosicionInversion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PosicionInversionMapper {

  @Mapping(target = "instantaneaId", source = "instantanea.id")
  PosicionInversion toDomain(PosicionInversionMO mo);

  PosicionInversionMO toMO(PosicionInversion domain);
}
