package com.centimo.api.mappers;

import com.centimo.api.domain.models.Compromiso;
import com.centimo.api.dto.CompromisoDto;
import com.centimo.api.dto.CreateCompromisoRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompromisoApiMapper {

  @Mapping(target = "description", source = "descripcion")
  @Mapping(target = "month", source = "mes")
  @Mapping(target = "year", source = "anio")
  @Mapping(target = "type", source = "tipo")
  @Mapping(target = "category", source = "categoria")
  @Mapping(target = "amount", source = "cantidad")
  @Mapping(target = "isEstimated", source = "esEstimado")
  CompromisoDto toDto(Compromiso domain);

  @Mapping(target = "descripcion", source = "description")
  @Mapping(target = "mes", source = "month")
  @Mapping(target = "anio", source = "year")
  @Mapping(target = "tipo", source = "type")
  @Mapping(target = "categoria", source = "category")
  @Mapping(target = "cantidad", source = "amount")
  @Mapping(target = "esEstimado", source = "isEstimated")
  @Mapping(target = "fechaCreacion", ignore = true)
  @Mapping(target = "fechaActualizacion", ignore = true)
  Compromiso toDomain(CreateCompromisoRequest request);
}
