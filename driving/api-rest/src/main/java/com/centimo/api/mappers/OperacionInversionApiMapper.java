package com.centimo.api.mappers;

import com.centimo.api.domain.models.OperacionInversion;
import com.centimo.api.dto.CreateOperacionInversionRequest;
import com.centimo.api.dto.OperacionInversionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OperacionInversionApiMapper {

  @Mapping(target = "accountId", source = "cuentaId")
  @Mapping(target = "assetName", source = "nombreActivo")
  @Mapping(target = "assetType", source = "tipoActivo")
  @Mapping(target = "type", source = "tipo")
  @Mapping(target = "buyDate", source = "fechaCompra")
  @Mapping(target = "buyQuantity", source = "cantidadCompra")
  @Mapping(target = "buyPricePerUnit", source = "precioUnitarioCompra")
  @Mapping(target = "buyTotalCost", source = "costeTotalCompra")
  @Mapping(target = "sellDate", source = "fechaVenta")
  @Mapping(target = "sellPricePerUnit", source = "precioUnitarioVenta")
  @Mapping(target = "sellTotalReceived", source = "cantidadTotalRecibida")
  @Mapping(target = "sellQuantity", source = "cantidadVenta")
  @Mapping(target = "pnl", source = "gananciaPerdida")
  @Mapping(target = "status", source = "estado")
  OperacionInversionDto toDto(OperacionInversion domain);

  @Mapping(target = "cuentaId", source = "accountId")
  @Mapping(target = "nombreActivo", source = "assetName")
  @Mapping(target = "tipoActivo", source = "assetType")
  @Mapping(target = "tipo", source = "type")
  @Mapping(target = "fechaCompra", source = "buyDate")
  @Mapping(target = "cantidadCompra", source = "buyQuantity")
  @Mapping(target = "precioUnitarioCompra", source = "buyPricePerUnit")
  @Mapping(target = "costeTotalCompra", source = "buyTotalCost")
  @Mapping(target = "fechaVenta", source = "sellDate")
  @Mapping(target = "precioUnitarioVenta", source = "sellPricePerUnit")
  @Mapping(target = "cantidadTotalRecibida", source = "sellTotalReceived")
  @Mapping(target = "cantidadVenta", source = "sellQuantity")
  @Mapping(target = "gananciaPerdida", source = "pnl")
  @Mapping(target = "estado", source = "status")
  @Mapping(target = "fechaCreacion", ignore = true)
  OperacionInversion toDomain(CreateOperacionInversionRequest request);
}
