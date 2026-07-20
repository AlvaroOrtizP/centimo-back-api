package com.centimo.api.mappers;

import com.centimo.api.domain.models.PosicionInversion;
import com.centimo.api.dto.CreatePosicionInversionRequest;
import com.centimo.api.dto.PosicionInversionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PosicionInversionApiMapper {

  @Mapping(target = "snapshotId", source = "instantaneaId")
  @Mapping(target = "assetName", source = "nombreActivo")
  @Mapping(target = "assetType", source = "tipoActivo")
  @Mapping(target = "quantity", source = "cantidad")
  @Mapping(target = "valuePerUnit", source = "valorUnitario")
  @Mapping(target = "totalValue", source = "valorTotal")
  PosicionInversionDto toDto(PosicionInversion domain);

  @Mapping(target = "instantaneaId", source = "snapshotId")
  @Mapping(target = "nombreActivo", source = "assetName")
  @Mapping(target = "tipoActivo", source = "assetType")
  @Mapping(target = "cantidad", source = "quantity")
  @Mapping(target = "valorUnitario", source = "valuePerUnit")
  @Mapping(target = "valorTotal", source = "totalValue")
  @Mapping(target = "fechaCreacion", ignore = true)
  PosicionInversion toDomain(CreatePosicionInversionRequest request);
}
