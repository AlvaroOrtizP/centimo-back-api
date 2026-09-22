package com.centimo.api.database.mappers;

import com.centimo.api.database.models.RevolutBalanceMO;
import com.centimo.api.domain.models.RevolutBalance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RevolutBalanceDatasourceMapper {

  RevolutBalance toDomain(RevolutBalanceMO mo);

  RevolutBalanceMO toEntity(RevolutBalance balance);
}
