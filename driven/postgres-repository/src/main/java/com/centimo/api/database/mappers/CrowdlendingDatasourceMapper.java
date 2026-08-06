package com.centimo.api.database.mappers;

import com.centimo.api.database.models.CrowdlendingInversionMO;
import com.centimo.api.domain.models.CrowdlendingInversion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CrowdlendingDatasourceMapper {

    @Mapping(target = "plataformaId", expression = "java(mo.getPlataforma() != null ? mo.getPlataforma().getId() : null)")
    CrowdlendingInversion toDomain(CrowdlendingInversionMO mo);

    CrowdlendingInversionMO toEntity(CrowdlendingInversion inversion);
}
