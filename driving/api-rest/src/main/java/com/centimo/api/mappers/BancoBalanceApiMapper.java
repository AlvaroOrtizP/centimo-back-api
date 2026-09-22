package com.centimo.api.mappers;

import com.centimo.api.domain.models.BancoBalance;
import com.centimo.api.dto.BancoBalanceRequest;
import com.centimo.api.dto.BancoBalanceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BancoBalanceApiMapper {

  BancoBalanceResponse toBancoBalanceResponse(BancoBalance balance);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  BancoBalance toDomain(BancoBalanceRequest request);
}