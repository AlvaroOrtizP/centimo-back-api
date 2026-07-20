package com.centimo.api.mappers;

import com.centimo.api.domain.models.InversionCrowdlending;
import com.centimo.api.dto.CreateInversionCrowdlendingRequest;
import com.centimo.api.dto.InversionCrowdlendingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InversionCrowdlendingApiMapper {

  @Mapping(target = "platformId", source = "plataformaId")
  @Mapping(target = "projectName", source = "nombreProyecto")
  @Mapping(target = "investedAmount", source = "cantidadInvertida")
  @Mapping(target = "interestRate", source = "tipoInteres")
  @Mapping(target = "termMonths", source = "plazoMeses")
  @Mapping(target = "startDate", source = "fechaInicio")
  @Mapping(target = "endDate", source = "fechaFin")
  @Mapping(target = "monthlyReturn", source = "retornoMensual")
  @Mapping(target = "totalReturned", source = "totalDevuelto")
  @Mapping(target = "status", source = "estado")
  InversionCrowdlendingDto toDto(InversionCrowdlending domain);

  @Mapping(target = "plataformaId", source = "platformId")
  @Mapping(target = "nombreProyecto", source = "projectName")
  @Mapping(target = "cantidadInvertida", source = "investedAmount")
  @Mapping(target = "tipoInteres", source = "interestRate")
  @Mapping(target = "plazoMeses", source = "termMonths")
  @Mapping(target = "fechaInicio", source = "startDate")
  @Mapping(target = "fechaFin", source = "endDate")
  @Mapping(target = "retornoMensual", source = "monthlyReturn")
  @Mapping(target = "totalDevuelto", source = "totalReturned")
  @Mapping(target = "estado", source = "status")
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  InversionCrowdlending toDomain(CreateInversionCrowdlendingRequest request);
}
