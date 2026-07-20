package com.centimo.api.database.mappers;

import com.centimo.api.database.models.CompromisoMO;
import com.centimo.api.domain.models.Compromiso;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompromisoMapper {

  Compromiso toDomain(CompromisoMO mo);

  CompromisoMO toMO(Compromiso domain);
}
