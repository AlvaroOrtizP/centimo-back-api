package com.centimo.api.database.mappers;

import com.centimo.api.database.models.EquitoBalanceMO;
import com.centimo.api.domain.models.EquitoBalance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EquitoBalanceDatasourceMapper {

  EquitoBalance toDomain(EquitoBalanceMO mo);

  EquitoBalanceMO toEntity(EquitoBalance balance);
}