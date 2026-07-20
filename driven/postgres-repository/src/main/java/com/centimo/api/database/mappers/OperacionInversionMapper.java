package com.centimo.api.database.mappers;

import com.centimo.api.database.models.OperacionInversionMO;
import com.centimo.api.domain.models.OperacionInversion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OperacionInversionMapper {

  @Mapping(target = "cuentaId", source = "cuenta.id")
  OperacionInversion toDomain(OperacionInversionMO mo);

  OperacionInversionMO toMO(OperacionInversion domain);
}
