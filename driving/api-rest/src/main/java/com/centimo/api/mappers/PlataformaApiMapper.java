package com.centimo.api.mappers;

import com.centimo.api.domain.enums.TipoPlataforma;
import com.centimo.api.domain.models.Plataforma;
import com.centimo.api.dto.Platform;
import com.centimo.api.dto.PlatformType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlataformaApiMapper {

  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "nombre")
  @Mapping(target = "type", expression = "java(toPlatformType(plataforma.getTipo()))")
  @Mapping(target = "color", source = "color")
  @Mapping(target = "icon", source = "icono")
  @Mapping(target = "order", source = "orden")
  @Mapping(target = "fixedNotes", source = "notasFijas")
  Platform toPlatform(Plataforma plataforma);

  default PlatformType toPlatformType(TipoPlataforma tipo) {
    if (tipo == null) return null;
    return switch (tipo) {
      case banco -> PlatformType.BANK;
      case inversion -> PlatformType.INVESTMENT;
      case cripto -> PlatformType.CRYPTO;
      case p2p -> PlatformType.P2P;
      case crowdlending -> PlatformType.CROWDLENDING;
    };
  }
}
