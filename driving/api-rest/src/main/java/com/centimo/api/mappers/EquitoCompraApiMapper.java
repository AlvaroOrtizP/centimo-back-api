package com.centimo.api.mappers;

import com.centimo.api.domain.models.EquitoCompra;
import com.centimo.api.dto.EquitoCompraCreate;
import com.centimo.api.dto.EquitoCompraResponse;
import com.centimo.api.dto.EquitoCompraUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EquitoCompraApiMapper {

  EquitoCompraResponse toEquitoCompraResponse(EquitoCompra compra);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  EquitoCompra toDomain(EquitoCompraCreate request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  EquitoCompra toDomain(EquitoCompraUpdate request);
}