package com.centimo.api.mappers;

import com.centimo.api.domain.models.B100Balance;
import com.centimo.api.dto.B100BalanceRequest;
import com.centimo.api.dto.B100BalanceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface B100BalanceApiMapper {

  B100BalanceResponse toB100BalanceResponse(B100Balance balance);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  B100Balance toDomain(B100BalanceRequest request);
}