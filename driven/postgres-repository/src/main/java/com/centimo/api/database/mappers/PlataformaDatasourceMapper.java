package com.centimo.api.database.mappers;

import com.centimo.api.database.models.PlataformaMO;
import com.centimo.api.domain.models.Plataforma;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlataformaDatasourceMapper {

    Plataforma toDomain(PlataformaMO mo);

    PlataformaMO toEntity(Plataforma plataforma);
}
