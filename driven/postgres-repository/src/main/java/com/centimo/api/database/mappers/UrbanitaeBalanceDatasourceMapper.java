package com.centimo.api.database.mappers;

import com.centimo.api.database.models.UrbanitaeBalanceMO;
import com.centimo.api.domain.models.UrbanitaeBalance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UrbanitaeBalanceDatasourceMapper {

  UrbanitaeBalance toDomain(UrbanitaeBalanceMO mo);

  UrbanitaeBalanceMO toEntity(UrbanitaeBalance balance);
}