package com.centimo.api.mappers;

import com.centimo.api.domain.models.Gasto;
import com.centimo.api.dto.CreateGastoRequest;
import com.centimo.api.dto.GastoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GastoApiMapper {

  @Mapping(target = "snapshotId", source = "instantaneaId")
  @Mapping(target = "category", source = "categoria")
  @Mapping(target = "amount", source = "cantidad")
  @Mapping(target = "date", source = "fecha")
  @Mapping(target = "description", source = "descripcion")
  GastoDto toDto(Gasto domain);

  @Mapping(target = "instantaneaId", source = "snapshotId")
  @Mapping(target = "categoria", source = "category")
  @Mapping(target = "cantidad", source = "amount")
  @Mapping(target = "fecha", source = "date")
  @Mapping(target = "descripcion", source = "description")
  @Mapping(target = "fechaCreacion", ignore = true)
  Gasto toDomain(CreateGastoRequest request);
}
