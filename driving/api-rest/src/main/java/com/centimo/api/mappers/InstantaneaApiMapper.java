package com.centimo.api.mappers;

import com.centimo.api.domain.models.InstantaneaMensual;
import com.centimo.api.dto.CreateInstantaneaRequest;
import com.centimo.api.dto.InstantaneaDto;
import com.centimo.api.dto.UpdateInstantaneaRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InstantaneaApiMapper {

  @Mapping(target = "accountId", source = "cuentaId")
  @Mapping(target = "year", source = "anio")
  @Mapping(target = "month", source = "mes")
  @Mapping(target = "balance", source = "saldo")
  @Mapping(target = "income", source = "ingresos")
  @Mapping(target = "expenses", source = "gastos")
  @Mapping(target = "contribution", source = "aportacion")
  @Mapping(target = "notes", source = "notas")
  InstantaneaDto toDto(InstantaneaMensual domain);

  @Mapping(target = "cuentaId", source = "accountId")
  @Mapping(target = "anio", source = "year")
  @Mapping(target = "mes", source = "month")
  @Mapping(target = "saldo", source = "balance")
  @Mapping(target = "ingresos", source = "income")
  @Mapping(target = "gastos", source = "expenses")
  @Mapping(target = "aportacion", source = "contribution")
  @Mapping(target = "notas", source = "notes")
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  InstantaneaMensual toDomain(CreateInstantaneaRequest request);

  @Mapping(target = "saldo", source = "balance")
  @Mapping(target = "ingresos", source = "income")
  @Mapping(target = "gastos", source = "expenses")
  @Mapping(target = "aportacion", source = "contribution")
  @Mapping(target = "notas", source = "notes")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "cuentaId", ignore = true)
  @Mapping(target = "anio", ignore = true)
  @Mapping(target = "mes", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  InstantaneaMensual toDomain(UpdateInstantaneaRequest request);
}
