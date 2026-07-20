package com.centimo.api.mappers;

import com.centimo.api.domain.models.FondoMyInvestor;
import com.centimo.api.dto.CreateFondoMyInvestorRequest;
import com.centimo.api.dto.FondoMyInvestorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FondoMyInvestorApiMapper {

  @Mapping(target = "code", source = "codigoIsin")
  @Mapping(target = "name", source = "nombre")
  FondoMyInvestorDto toDto(FondoMyInvestor domain);

  @Mapping(target = "codigoIsin", source = "code")
  @Mapping(target = "nombre", source = "name")
  @Mapping(target = "fechaCreacion", ignore = true)
  FondoMyInvestor toDomain(CreateFondoMyInvestorRequest request);
}
