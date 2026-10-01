package com.centimo.api.mappers;

import com.centimo.api.domain.models.UrbanitaeBalance;
import com.centimo.api.dto.UrbanitaeBalanceRequest;
import com.centimo.api.dto.UrbanitaeBalanceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UrbanitaeBalanceApiMapper {

  UrbanitaeBalanceResponse toUrbanitaeBalanceResponse(UrbanitaeBalance balance);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  UrbanitaeBalance toDomain(UrbanitaeBalanceRequest request);
}