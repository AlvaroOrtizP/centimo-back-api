package com.centimo.api.mappers;

import com.centimo.api.domain.models.FondoMyInvestor;
import com.centimo.api.dto.MyInvestorFund;
import com.centimo.api.dto.MyInvestorFundCreate;
import com.centimo.api.dto.MyInvestorFundUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MyInvestorFundApiMapper {

  @Mapping(target = "code", source = "codigoIsin")
  @Mapping(target = "name", source = "nombre")
  @Mapping(target = "tipo", source = "tipo")
  MyInvestorFund toMyInvestorFundResponse(FondoMyInvestor fondo);

  @Mapping(target = "codigoIsin", source = "code")
  @Mapping(target = "nombre", source = "name")
  @Mapping(target = "tipo", source = "tipo")
  @Mapping(target = "fechaCreacion", ignore = true)
  FondoMyInvestor toDomain(MyInvestorFundCreate request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "codigoIsin", source = "code")
  @Mapping(target = "nombre", source = "name")
  @Mapping(target = "tipo", source = "tipo")
  @Mapping(target = "fechaCreacion", ignore = true)
  FondoMyInvestor toDomain(MyInvestorFundUpdate request);
}