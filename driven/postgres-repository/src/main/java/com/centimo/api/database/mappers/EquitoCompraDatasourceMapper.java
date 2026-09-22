package com.centimo.api.database.mappers;

import com.centimo.api.database.models.EquitoCompraMO;
import com.centimo.api.domain.models.EquitoCompra;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EquitoCompraDatasourceMapper {

  EquitoCompra toDomain(EquitoCompraMO mo);

  EquitoCompraMO toEntity(EquitoCompra compra);
}