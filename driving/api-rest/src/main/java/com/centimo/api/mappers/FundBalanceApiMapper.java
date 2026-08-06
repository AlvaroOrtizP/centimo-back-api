package com.centimo.api.mappers;

import com.centimo.api.domain.models.BalanceFondo;
import com.centimo.api.dto.FundBalance;
import com.centimo.api.dto.FundBalanceCreate;
import com.centimo.api.dto.FundBalanceUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FundBalanceApiMapper {

  @Mapping(target = "fundId", source = "fondoId")
  @Mapping(target = "year", source = "anio")
  @Mapping(target = "month", source = "mes")
  @Mapping(target = "balance", source = "saldo")
  @Mapping(target = "income", source = "intereses")
  @Mapping(target = "contribution", source = "aportacion")
  @Mapping(target = "expenses", source = "retirada")
  FundBalance toFundBalance(BalanceFondo balance);

  @Mapping(target = "fondoId", source = "fundId")
  @Mapping(target = "anio", source = "year")
  @Mapping(target = "mes", source = "month")
  @Mapping(target = "saldo", source = "balance")
  @Mapping(target = "intereses", source = "income")
  @Mapping(target = "aportacion", source = "contribution")
  @Mapping(target = "retirada", source = "expenses")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  BalanceFondo toDomain(FundBalanceCreate create);

  @Mapping(target = "saldo", source = "balance")
  @Mapping(target = "intereses", source = "income")
  @Mapping(target = "aportacion", source = "contribution")
  @Mapping(target = "retirada", source = "expenses")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fondoId", ignore = true)
  @Mapping(target = "anio", ignore = true)
  @Mapping(target = "mes", ignore = true)
  @Mapping(target = "fechaCreacion", ignore = true)
  BalanceFondo toDomain(FundBalanceUpdate update);
}
