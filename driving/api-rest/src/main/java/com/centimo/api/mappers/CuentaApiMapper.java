package com.centimo.api.mappers;

import com.centimo.api.domain.enums.TipoCuenta;
import com.centimo.api.domain.models.Cuenta;
import com.centimo.api.dto.Account;
import com.centimo.api.dto.AccountType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CuentaApiMapper {

  @Mapping(target = "id", source = "id")
  @Mapping(target = "platformId", source = "plataformaId")
  @Mapping(target = "name", source = "nombre")
  @Mapping(target = "type", expression = "java(toAccountType(cuenta.getTipo()))")
  @Mapping(target = "currency", source = "moneda")
  @Mapping(target = "order", source = "orden")
  Account toAccount(Cuenta cuenta);

  default AccountType toAccountType(TipoCuenta tipo) {
    if (tipo == null) return null;
    return switch (tipo) {
      case corriente -> AccountType.CHECKING;
      case ahorro -> AccountType.SAVINGS;
      case inversion -> AccountType.INVESTMENT;
      case bolsillo -> AccountType.POCKET;
    };
  }
}
