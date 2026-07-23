package com.centimo.api.database.mappers;

import com.centimo.api.database.models.InstantaneaMensualMO;
import com.centimo.api.domain.models.InstantaneaMensual;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InstantaneaMensualMapper {

    @Mapping(target = "cuentaId", expression = "java(mo.getCuenta() != null ? mo.getCuenta().getId() : null)")
    InstantaneaMensual toDomain(InstantaneaMensualMO mo);

    InstantaneaMensualMO toEntity(InstantaneaMensual instantanea);
}
