package com.centimo.api.mappers;

import com.centimo.api.domain.models.BalanceFondo;
import com.centimo.api.dto.BalanceFondoDto;
import com.centimo.api.dto.CreateBalanceFondoRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceFondoApiMapper {

  @Mapping(target = "fundId", source = "fondoId")
  @Mapping(target = "year", source = "anio")
  @Mapping(target = "month", source = "mes")
  @Mapping(target = "balance", source = "saldo")
  BalanceFondoDto toDto(BalanceFondo domain);

  @Mapping(target = "fondoId", source = "fundId")
  @Mapping(target = "anio", source = "year")
  @Mapping(target = "mes", source = "month")
  @Mapping(target = "saldo", source = "balance")
  @Mapping(target = "fechaCreacion", ignore = true)
  BalanceFondo toDomain(CreateBalanceFondoRequest request);
}
