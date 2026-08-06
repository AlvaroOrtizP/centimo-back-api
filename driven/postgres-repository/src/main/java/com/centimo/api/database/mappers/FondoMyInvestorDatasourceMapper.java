package com.centimo.api.database.mappers;

import com.centimo.api.database.models.FondoMyInvestorMO;
import com.centimo.api.domain.models.FondoMyInvestor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FondoMyInvestorDatasourceMapper {

  FondoMyInvestor toDomain(FondoMyInvestorMO mo);

  FondoMyInvestorMO toEntity(FondoMyInvestor fondo);
}
