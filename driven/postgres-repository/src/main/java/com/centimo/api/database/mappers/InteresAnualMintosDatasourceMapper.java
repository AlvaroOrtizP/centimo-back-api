package com.centimo.api.database.mappers;

import com.centimo.api.database.models.InteresAnualMintosMO;
import com.centimo.api.domain.models.InteresAnualMintos;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InteresAnualMintosDatasourceMapper {

  InteresAnualMintos toDomain(InteresAnualMintosMO mo);

  InteresAnualMintosMO toEntity(InteresAnualMintos interes);
}
