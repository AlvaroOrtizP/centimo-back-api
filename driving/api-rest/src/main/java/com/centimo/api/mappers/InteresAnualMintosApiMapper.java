package com.centimo.api.mappers;

import com.centimo.api.domain.models.InteresAnualMintos;
import com.centimo.api.dto.MintosInterestAnnual;
import com.centimo.api.dto.MintosInterestAnnualCreate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InteresAnualMintosApiMapper {

  @Mapping(target = "year", source = "anio")
  @Mapping(target = "amount", source = "cantidad")
  @Mapping(target = "taxWithholding", source = "retencionImpuestos")
  @Mapping(target = "taxRate", source = "tipoImpositivo")
  @Mapping(target = "netAmount", source = "importeNeto")
  MintosInterestAnnual toMintosInterestAnnual(InteresAnualMintos interes);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "anio", source = "year")
  @Mapping(target = "cantidad", source = "amount")
  @Mapping(target = "retencionImpuestos", source = "taxWithholding")
  @Mapping(target = "tipoImpositivo", source = "taxRate")
  @Mapping(target = "importeNeto", source = "netAmount")
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  InteresAnualMintos toDomain(MintosInterestAnnualCreate create);
}
