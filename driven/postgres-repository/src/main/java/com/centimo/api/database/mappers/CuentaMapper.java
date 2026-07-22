package com.centimo.api.database.mappers;

import com.centimo.api.database.models.CuentaMO;
import com.centimo.api.domain.models.Cuenta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CuentaMapper {

  @Mapping(target = "plataformaId", expression = "java(mo.getPlataforma() != null ? mo.getPlataforma().getId() : null)")
  Cuenta toDomain(CuentaMO mo);

  @Mapping(target = "plataforma", ignore = true)
  @Mapping(target = "plataformaId", ignore = true)
  CuentaMO toMO(Cuenta domain);
}
