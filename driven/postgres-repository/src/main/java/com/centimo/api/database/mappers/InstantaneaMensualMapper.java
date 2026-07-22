package com.centimo.api.database.mappers;

import com.centimo.api.database.models.InstantaneaMensualMO;
import com.centimo.api.domain.models.InstantaneaMensual;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InstantaneaMensualMapper {

  @Mapping(target = "cuentaId", expression = "java(mo.getCuenta() != null ? mo.getCuenta().getId() : null)")
  InstantaneaMensual toDomain(InstantaneaMensualMO mo);

  @Mapping(target = "cuenta", ignore = true)
  @Mapping(target = "cuentaId", ignore = true)
  InstantaneaMensualMO toMO(InstantaneaMensual domain);

}
