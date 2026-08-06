package com.centimo.api.database.mappers;

import com.centimo.api.database.models.BalanceFondoMO;
import com.centimo.api.domain.models.BalanceFondo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceFondoDatasourceMapper {

  @Mapping(target = "fondoId", expression = "java(mo.getFondo() != null ? mo.getFondo().getId() : null)")
  BalanceFondo toDomain(BalanceFondoMO mo);

  BalanceFondoMO toEntity(BalanceFondo balance);
}
