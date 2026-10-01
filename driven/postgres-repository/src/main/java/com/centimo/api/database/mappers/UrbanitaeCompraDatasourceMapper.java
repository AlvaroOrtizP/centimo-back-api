package com.centimo.api.database.mappers;

import com.centimo.api.database.models.UrbanitaeCompraMO;
import com.centimo.api.domain.models.UrbanitaeCompra;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UrbanitaeCompraDatasourceMapper {

  UrbanitaeCompra toDomain(UrbanitaeCompraMO mo);

  UrbanitaeCompraMO toEntity(UrbanitaeCompra compra);
}