package com.centimo.api.mappers;

import com.centimo.api.domain.models.UrbanitaeCompra;
import com.centimo.api.dto.UrbanitaeCompraCreate;
import com.centimo.api.dto.UrbanitaeCompraResponse;
import com.centimo.api.dto.UrbanitaeCompraUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UrbanitaeCompraApiMapper {

  UrbanitaeCompraResponse toUrbanitaeCompraResponse(UrbanitaeCompra compra);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  UrbanitaeCompra toDomain(UrbanitaeCompraCreate request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  UrbanitaeCompra toDomain(UrbanitaeCompraUpdate request);
}