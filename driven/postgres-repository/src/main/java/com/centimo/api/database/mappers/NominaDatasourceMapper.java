package com.centimo.api.database.mappers;

import com.centimo.api.database.models.NominaMO;
import com.centimo.api.domain.models.Nomina;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NominaDatasourceMapper {

  Nomina toDomain(NominaMO mo);

  NominaMO toEntity(Nomina nomina);
}