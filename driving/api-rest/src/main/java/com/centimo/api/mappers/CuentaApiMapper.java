package com.centimo.api.mappers;

import com.centimo.api.domain.models.Cuenta;
import com.centimo.api.dto.CreateCuentaRequest;
import com.centimo.api.dto.CuentaDto;
import com.centimo.api.dto.UpdateCuentaRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CuentaApiMapper {

  @Mapping(target = "platformId", source = "plataformaId")
  @Mapping(target = "name", source = "nombre")
  @Mapping(target = "type", source = "tipo")
  @Mapping(target = "currency", source = "moneda")
  @Mapping(target = "order", source = "orden")
  CuentaDto toDto(Cuenta domain);

  @Mapping(target = "plataformaId", source = "platformId")
  @Mapping(target = "nombre", source = "name")
  @Mapping(target = "tipo", source = "type")
  @Mapping(target = "moneda", source = "currency")
  @Mapping(target = "orden", source = "order")
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  Cuenta toDomain(CreateCuentaRequest request);

  @Mapping(target = "plataformaId", source = "platformId")
  @Mapping(target = "nombre", source = "name")
  @Mapping(target = "tipo", source = "type")
  @Mapping(target = "moneda", source = "currency")
  @Mapping(target = "orden", source = "order")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  Cuenta toDomain(UpdateCuentaRequest request);
}
