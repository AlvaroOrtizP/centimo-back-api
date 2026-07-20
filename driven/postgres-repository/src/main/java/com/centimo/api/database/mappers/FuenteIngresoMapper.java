package com.centimo.api.database.mappers;

import com.centimo.api.database.models.FuenteIngresoMO;
import com.centimo.api.domain.models.FuenteIngreso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FuenteIngresoMapper {

  @Mapping(target = "instantaneaId", source = "instantanea.id")
  FuenteIngreso toDomain(FuenteIngresoMO mo);

  FuenteIngresoMO toMO(FuenteIngreso domain);
}
