package com.centimo.api.mappers;

import com.centimo.api.domain.models.CrowdlendingInversion;
import com.centimo.api.dto.CrowdlendingInvestment;
import com.centimo.api.dto.CrowdlendingInvestmentCreate;
import com.centimo.api.dto.ProjectStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CrowdlendingApiMapper {

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
  CrowdlendingInvestment toCrowdlendingInvestment(CrowdlendingInversion inversion);

  @Mapping(target = "id", ignore = true)
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
  CrowdlendingInversion toDomain(CrowdlendingInvestmentCreate create);

  default ProjectStatus toProjectStatus(String estado) {
    return ProjectStatus.fromValue(estado);
  }

  default String fromProjectStatus(ProjectStatus status) {
    return status.getValue();
  }
}
