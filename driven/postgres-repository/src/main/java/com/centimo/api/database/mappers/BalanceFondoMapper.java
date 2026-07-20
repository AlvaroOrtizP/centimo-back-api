package com.centimo.api.database.mappers;

import com.centimo.api.database.models.BalanceFondoMO;
import com.centimo.api.domain.models.BalanceFondo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceFondoMapper {

  @Mapping(target = "fondoId", source = "fondo.id")
  BalanceFondo toDomain(BalanceFondoMO mo);

  BalanceFondoMO toMO(BalanceFondo domain);
}
