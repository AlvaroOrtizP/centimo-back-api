package com.centimo.api.database.mappers;

import com.centimo.api.database.models.BancoBalanceMO;
import com.centimo.api.domain.models.BancoBalance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BancoBalanceDatasourceMapper {

  BancoBalance toDomain(BancoBalanceMO mo);

  BancoBalanceMO toEntity(BancoBalance balance);
}