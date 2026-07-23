package com.centimo.api.database.mappers;

import com.centimo.api.database.models.GastoMO;
import com.centimo.api.domain.models.Gasto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GastoDatasourceMapper {

    @Mapping(target = "instantaneaId", expression = "java(mo.getInstantanea() != null ? mo.getInstantanea().getId() : null)")
    Gasto toDomain(GastoMO mo);

    GastoMO toEntity(Gasto gasto);
}
