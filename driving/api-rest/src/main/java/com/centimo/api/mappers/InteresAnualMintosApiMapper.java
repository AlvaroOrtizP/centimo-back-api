package com.centimo.api.mappers;

import com.centimo.api.domain.models.InteresAnualMintos;
import com.centimo.api.dto.MintosInterestAnnual;
import com.centimo.api.dto.MintosInterestAnnualCreate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InteresAnualMintosApiMapper {

  MintosInterestAnnual toMintosInterestAnnual(InteresAnualMintos interes);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  InteresAnualMintos toDomain(MintosInterestAnnualCreate request);
}