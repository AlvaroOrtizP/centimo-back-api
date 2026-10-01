package com.centimo.api.database.mappers;

import com.centimo.api.database.models.B100BalanceMO;
import com.centimo.api.domain.models.B100Balance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface B100BalanceDatasourceMapper {

  B100Balance toDomain(B100BalanceMO mo);

  B100BalanceMO toEntity(B100Balance balance);
}