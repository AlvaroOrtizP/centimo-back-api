package com.centimo.api.mappers;

import com.centimo.api.domain.models.RevolutBalance;
import com.centimo.api.dto.RevolutBalanceRequest;
import com.centimo.api.dto.RevolutBalanceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RevolutBalanceApiMapper {

  RevolutBalanceResponse toRevolutBalanceResponse(RevolutBalance balance);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  RevolutBalance toDomain(RevolutBalanceRequest request);
}