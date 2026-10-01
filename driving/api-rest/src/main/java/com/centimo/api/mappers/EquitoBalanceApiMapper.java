package com.centimo.api.mappers;

import com.centimo.api.domain.models.EquitoBalance;
import com.centimo.api.dto.EquitoBalanceRequest;
import com.centimo.api.dto.EquitoBalanceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EquitoBalanceApiMapper {

  EquitoBalanceResponse toEquitoBalanceResponse(EquitoBalance balance);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  EquitoBalance toDomain(EquitoBalanceRequest request);
}