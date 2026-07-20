package com.centimo.api.mappers;

import com.centimo.api.domain.models.FuenteIngreso;
import com.centimo.api.dto.CreateFuenteIngresoRequest;
import com.centimo.api.dto.FuenteIngresoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FuenteIngresoApiMapper {

  @Mapping(target = "snapshotId", source = "instantaneaId")
  @Mapping(target = "source", source = "fuente")
  @Mapping(target = "description", source = "descripcion")
  @Mapping(target = "amount", source = "cantidad")
  FuenteIngresoDto toDto(FuenteIngreso domain);

  @Mapping(target = "instantaneaId", source = "snapshotId")
  @Mapping(target = "fuente", source = "source")
  @Mapping(target = "descripcion", source = "description")
  @Mapping(target = "cantidad", source = "amount")
  @Mapping(target = "fechaCreacion", ignore = true)
  FuenteIngreso toDomain(CreateFuenteIngresoRequest request);
}
